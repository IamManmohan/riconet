package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.enums.CollectionEventType;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.TransactionManagerService;
import com.rivigo.riconet.core.service.TransportationPartnerMappingService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.depositslip.ConsignmentDepositSlipRepository;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.util.CollectionUtils;

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

  private final PickupService pickupService;

  private final PaymentDetailV2Service paymentDetailV2Service;

  private final ConsignmentScheduleService consignmentScheduleService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  private final ConsignmentDepositSlipRepository consignmentDepositSlipRepository;

  private final TransportationPartnerMappingService transportationPartnerMappingService;

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
  public void syncExclusion(
      Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap,
      Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map) {
    if (MapUtils.isEmpty(cnIdToConsignmentMap)) {
      return;
    }
    Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(cnIdToConsignmentMap.keySet());
    Map<Long, Location> locationMap = locationService.getLocationMap();
    Map<Long, String> pickupToUser =
        getUserByPickupId(
            cnIdToConsignmentMap
                .values()
                .stream()
                .map(ConsignmentReadOnly::getPickupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    Map<Long, String> drsToUser =
        getUserByDRSId(
            cnIdToConsignmentMap
                .values()
                .stream()
                .map(ConsignmentReadOnly::getDrsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

    buildRequestDtoAndSendEvents(
        Lists.newArrayList(cnIdToPaymentDetailV2Map.values()),
        cnIdToConsignmentMap,
        consignmentScheduleMap,
        locationMap,
        pickupToUser,
        drsToUser,
        ZoomEventType.HANDOVER_COLLECTION_EXCLUDE);
  }

  private Map<Long, String> getUserByPickupId(List<Long> pickupIds) {
    if (CollectionUtils.isEmpty(pickupIds)) return new HashMap<>();
    List<Pickup> pickups = pickupService.getPickups(pickupIds);
    List<Long> userIds =
        pickups
            .stream()
            .map(Pickup::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    Map<Long, String> userEmailMap = userMasterService.getUserEmailMap(userIds);
    return pickups
        .stream()
        .filter(p -> userEmailMap.containsKey(p.getUserId()))
        .collect(Collectors.toMap(Pickup::getId, p -> userEmailMap.get(p.getUserId())));
  }

  private Map<Long, String> getUserByDRSId(List<Long> drsIds) {
    if (CollectionUtils.isEmpty(drsIds)) return new HashMap<>();
    Map<Long, Long> userIdByDrs = transportationPartnerMappingService.getUserIdByDrsId(drsIds);
    Map<Long, String> userEmailMap = userMasterService.getUserEmailMap(userIdByDrs.values());
    Map<Long, String> userByDsr = new HashMap<>();
    userIdByDrs.forEach((dsrId, userId) -> userByDsr.put(dsrId, userEmailMap.get(userId)));
    return userByDsr;
  }

  @Override
  public void syncPostUnpost(
      HandoverCollectionEventPayload handoverCollectionEventPayload, ZoomEventType zoomEventType) {
    List<Long> consignmentIds =
        consignmentDepositSlipRepository.findConsignmentIdByDepositSlipId(
            handoverCollectionEventPayload.getDepositSlipId());
    Map<Long, ConsignmentReadOnly> consignmentMap =
        consignmentReadOnlyService.getConsignmentMap(consignmentIds);
    List<PaymentDetailV2> paymentDetails =
        paymentDetailV2Service.getByConsignmentIdIn(consignmentIds);
    Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(consignmentIds);
    Map<Long, Location> locationMap = locationService.getLocationMap();

    buildRequestDtoAndSendEvents(
        paymentDetails,
        consignmentMap,
        consignmentScheduleMap,
        locationMap,
        Collections.emptyMap(),
        Collections.emptyMap(),
        zoomEventType);
  }

  private void buildRequestDtoAndSendEvents(
      List<PaymentDetailV2> paymentDetails,
      Map<Long, ConsignmentReadOnly> consignmentMap,
      Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap,
      Map<Long, Location> locationMap,
      Map<Long, String> pickupToUser,
      Map<Long, String> drsToUser,
      ZoomEventType zoomEventType) {

    List<CollectionRequestDto> collectionRequestDtos = new ArrayList<>();
    for (PaymentDetailV2 paymentDetailV2 : paymentDetails) {
      ConsignmentReadOnly consignment = consignmentMap.get(paymentDetailV2.getConsignmentId());
      CollectionRequestDto collectionRequestDto =
          CollectionRequestDto.builder()
              .consignmentId(consignment.getId())
              .cnote(consignment.getCnote())
              .eventType(getCollectionEventType(zoomEventType, paymentDetailV2.getPaymentType()))
              .amount(paymentDetailV2.getTotalRoundOffAmount().longValue())
              .bankTransferPendingApproval(paymentDetailV2.getBankName())
              .paymentType(paymentDetailV2.getPaymentMode())
              .captainCode(getCaptainCode(consignment, paymentDetailV2, pickupToUser, drsToUser))
              .build();
      collectionRequestDto =
          setLocationDetails(
              collectionRequestDto, consignmentScheduleMap.get(consignment.getId()), locationMap);
      collectionRequestDtos.add(collectionRequestDto);
    }
    sendEventsToTransactionManager(collectionRequestDtos);
  }

  private String getCaptainCode(
      ConsignmentReadOnly consignment,
      PaymentDetailV2 paymentDetailV2,
      Map<Long, String> pickupToUser,
      Map<Long, String> drsToUser) {
    if (paymentDetailV2.getPaymentMode() == PaymentMode.PAID) {
      Long pickupId = consignment.getPickupId();
      return pickupToUser.get(pickupId);
    } else {
      Long drsId = consignment.getDrsId();
      return drsToUser.get(drsId);
    }
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

  private CollectionEventType getCollectionEventType(
      ZoomEventType zoomEventType, PaymentType paymentType) {
    if (zoomEventType == ZoomEventType.HANDOVER_COLLECTION_POST) {
      if (paymentType == PaymentType.CASH) {
        return CollectionEventType.KNOCK_OFF_CASH;
      } else if (paymentType == PaymentType.CHEQUE) {
        return CollectionEventType.KNOCK_OFF_CHEQUE;
      } else {
        return CollectionEventType.KNOCK_OFF_BANK_TRANSFER;
      }
    } else if (zoomEventType == ZoomEventType.HANDOVER_COLLECTION_UNPOST) {
      if (paymentType == PaymentType.CASH) {
        return CollectionEventType.KNOCK_OFF_REVERT_CASH;
      } else if (paymentType == PaymentType.CHEQUE) {
        return CollectionEventType.KNOCK_OFF_REVERT_CHEQUE;
      } else {
        return CollectionEventType.KNOCK_OFF_REVERT_BANK_TRANSFER;
      }
    } else {
      if (paymentType == PaymentType.CHEQUE) {
        return CollectionEventType.CHEQUE_BOUNCE;
      }
      return CollectionEventType.CHEQUE_BOUNCE_BANK_TRANSFER;
    }
  }
}
