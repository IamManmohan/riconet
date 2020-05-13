package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.enums.CollectionEventType;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.TransactionManagerService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerServiceImpl implements TransactionManagerService {

  private final ObjectMapper objectMapper;

  private final SsoService ssoService;

  private final UserMasterService userMasterService;

  @Qualifier("defaultRestClientUtilityServiceImpl")
  private final RestClientUtilityService restClientUtilityService;

  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  @Value("${rivigo.sso.password}")
  private String ssoPassword;

  @Value("${transaction.manager.url}")
  private String transactionManagerBaseUrl;

  private final AccessTokenSsfRedisRepository accessTokenSsfRedisRepository;

  private final LocationService locationService;

  private final PaymentDetailV2Service paymentDetailV2Service;

  private final ConsignmentScheduleService consignmentScheduleService;

  @Override
  public void hitTransactionManagerAndLogResponse(
      @NonNull CollectionRequestDto collectionRequestDto) {

    User user = userMasterService.getByEmail(ssoUsername);

    String token = accessTokenSsfRedisRepository.get(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN);
    if (token == null) {
      log.info("No existing token found. New token is being generated ");
      token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
      accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("token", token);
    headers.add("userId", user.getId().toString());
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestJson = null;
    try {
      requestJson = objectMapper.writeValueAsString(collectionRequestDto);
    } catch (JsonProcessingException e) {
      log.error("Could not convert to string collectionRequestDto: {}", collectionRequestDto);
    }

    HttpEntity httpHeaders = new HttpEntity<>(requestJson, headers);

    log.debug("Hitting transaction manager with collectionRequestDto: {}", collectionRequestDto);

    restClientUtilityService.executeRest(
        transactionManagerBaseUrl + UrlConstant.TRANSACTION_MANAGER_URL,
        HttpMethod.POST,
        httpHeaders,
        String.class);
  }

  @Override
  public void syncExclusion(Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap) {
    if (MapUtils.isEmpty(cnIdToConsignmentMap)) {
      return;
    }
    List<PaymentDetailV2> paymentDetailV2s =
        paymentDetailV2Service.getByConsignmentIdIn(cnIdToConsignmentMap.keySet());
    Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(cnIdToConsignmentMap.keySet());
    Map<Long, Location> locationMap = locationService.getLocationMap();

    List<CollectionRequestDto> collectionRequestDtos = new ArrayList<>();
    for (PaymentDetailV2 paymentDetailV2 : paymentDetailV2s) {
      ConsignmentReadOnly consignmentReadOnly =
          cnIdToConsignmentMap.get(paymentDetailV2.getConsignmentId());
      CollectionRequestDto collectionRequestDto =
          CollectionRequestDto.builder()
              .consignmentId(consignmentReadOnly.getId())
              .cnote(consignmentReadOnly.getCnote())
              .eventType(getExclusionEventType(paymentDetailV2.getPaymentType()))
              .amount(paymentDetailV2.getTotalRoundOffAmount().longValue())
              .bankTransferPendingApproval(paymentDetailV2.getBankName())
              .paymentType(paymentDetailV2.getPaymentMode())
              .build();
      collectionRequestDto =
          setLocationDetails(
              collectionRequestDto,
              consignmentScheduleMap.get(consignmentReadOnly.getId()),
              locationMap);
      collectionRequestDtos.add(collectionRequestDto);
    }
    sendEventsToTransactionManager(collectionRequestDtos);
  }

  private void sendEventsToTransactionManager(List<CollectionRequestDto> collectionRequestDtos) {
    for (CollectionRequestDto collectionRequestDto : collectionRequestDtos) {
      try {
        hitTransactionManagerAndLogResponse(collectionRequestDto);
      } catch (Exception e) {
        log.error(
            "Error communicating with transaction manager for {}. Error - ",
            collectionRequestDto,
            e);
      }
    }
  }

  private CollectionRequestDto setLocationDetails(
      CollectionRequestDto collectionRequestDto,
      List<ConsignmentSchedule> consignmentSchedules,
      Map<Long, Location> locationMap) {
    consignmentSchedules =
        consignmentSchedules
            .stream()
            .filter(p -> LocationTypeV2.LOCATION.equals(p.getLocationType()))
            .filter(
                p -> !Arrays.asList(LocationTag.BF, LocationTag.DF).contains(p.getLocationTag()))
            .collect(Collectors.toList());

    consignmentSchedules
        .stream()
        .min(Comparator.comparing(ConsignmentSchedule::getSequence))
        .ifPresent(
            start ->
                collectionRequestDto.setPickupOuCode(
                    getLocationCode(locationMap, start.getLocationId())));
    consignmentSchedules
        .stream()
        .max(Comparator.comparing(ConsignmentSchedule::getSequence))
        .ifPresent(
            end ->
                collectionRequestDto.setDeliveryOuCode(
                    getLocationCode(locationMap, end.getLocationId())));
    return collectionRequestDto;
  }

  private String getLocationCode(Map<Long, Location> locationMap, Long locationId) {
    if (locationMap.containsKey(locationId)) {
      return locationMap.get(locationId).getCode();
    }
    return null;
  }

  private CollectionEventType getExclusionEventType(PaymentType paymentType) {
    if (paymentType == PaymentType.CHEQUE) {
      return CollectionEventType.CHEQUE_BOUNCE;
    }
    return CollectionEventType.CHEQUE_BOUNCE_BANK_TRANSFER;
  }
}
