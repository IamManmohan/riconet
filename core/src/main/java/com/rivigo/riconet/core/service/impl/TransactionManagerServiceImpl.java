package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.finance.utils.StringUtils;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PRSService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.TransactionManagerService;
import com.rivigo.riconet.core.service.TransportationPartnerMappingService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.transaction.manager.client.dto.CollectionRequestDto;
import com.rivigo.transaction.manager.client.enums.CollectionEventType;
import com.rivigo.transaction.manager.client.service.TransactionService;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.PickupRunSheet;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.depositslip.ConsignmentDepositSlipRepository;
import com.rivigo.zoom.util.rest.exception.ZoomRestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/** This class implements the interface of transaction manager service. */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerServiceImpl implements TransactionManagerService {

  /** object mapper bean for converting DTOs. */
  private final ObjectMapper objectMapper;

  /** bean of user master service for fetching details of the user. */
  private final UserMasterService userMasterService;

  /** sso user name. */
  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  /** transaction manager client key. */
  @Value("${transaction.manager.client.key}")
  private String transactionManagerClientKey;

  /** bean of prs service to fetch prs details. */
  private final PRSService prsService;

  /** bean of location service. */
  private final LocationService locationService;

  /** bean of PdV2 to get the payment details of consignment. */
  private final PaymentDetailV2Service paymentDetailV2Service;

  /** bean of consignment schedule service to get schedule details of consignment. */
  private final ConsignmentScheduleService consignmentScheduleService;

  /** bean of consignment read only service. */
  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  /** bean of consignment deposit slip repository. */
  private final ConsignmentDepositSlipRepository consignmentDepositSlipRepository;

  /** bean of transportation mapping service to get captain details. */
  private final TransportationPartnerMappingService transportationPartnerMappingService;

  /** bean of transaction service for hitting transaction manager. */
  private final TransactionService transactionService;

  /**
   * This function hits transaction manager with collectionRequestDtoJsonString and logs the
   * response.
   *
   * @param collectionRequestDtoJsonString request json string to be sent to transaction manager.
   */
  @Override
  public void hitTransactionManagerAndLogResponse(@NonNull String collectionRequestDtoJsonString) {
    log.info("Collection request payload {}", collectionRequestDtoJsonString);
    final CollectionRequestDto collectionRequestDto;
    try {
      collectionRequestDto =
          objectMapper.readValue(collectionRequestDtoJsonString, CollectionRequestDto.class);
    } catch (IOException e) {
      log.error("Transformation failed {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
    log.info("Converted to collection Request {}", collectionRequestDto);
    hitTransactionManagerAndLogResponse(collectionRequestDto);
  }

  /**
   * This function hits transaction manager with collectionRequestDto and logs the response.
   *
   * @param collectionRequestDto request dto to be sent to transaction manager.
   */
  @Override
  public void hitTransactionManagerAndLogResponse(
      @NonNull CollectionRequestDto collectionRequestDto) {

    log.debug("Hitting transaction manager with collectionRequestDto: {}", collectionRequestDto);

    try {
      final Optional<String> response =
          transactionService.createTransactions(
              getUserId(), transactionManagerClientKey, collectionRequestDto);
      response.ifPresent(s -> log.info("Response from hitting transaction manager: {}", s));
    } catch (ZoomRestException e) {
      log.error("Create transaction failed in transaction manager because: {}", e.getMessage());
    }
  }

  /**
   * This function hits transaction manager and rollbackTransactions with
   * collectionRequestDtoJsonString and logs the response.
   *
   * @param collectionRequestDtoJsonString request json string to be sent to transaction manager.
   */
  @Override
  public void rollbackTransactionsAndLogResponse(@NonNull String collectionRequestDtoJsonString) {

    log.info("RollBacking {}", collectionRequestDtoJsonString);
    final String reference = extractReference(collectionRequestDtoJsonString);
    if (StringUtils.isEmpty(reference)) {
      return;
    }

    log.info("Reference {}", reference);
    try {
      final Optional<String> response =
          transactionService.rollbackByReference(
              getUserId(), transactionManagerClientKey, reference);
      response.ifPresent(s -> log.info("Response from hitting transaction manager: {}", s));
    } catch (ZoomRestException e) {
      log.error("Rollback transaction failed in transaction manager because {}", e.getMessage());
    }
  }

  /**
   * This function is used to return user id to be sent to transaction manager service.
   *
   * @return user id of sso.
   */
  private String getUserId() {
    final User user = userMasterService.getByEmail(ssoUsername);
    return user.getId().toString();
  }

  /**
   * This function uses object mapper to convert json string to Collection request dto to get cnote.
   *
   * @param collectionRequestDtoString collection request dto in the form of a string.
   * @return cnote from collection request dto or else empty string.
   */
  private String extractReference(String collectionRequestDtoString) {
    try {
      return objectMapper
          .readValue(collectionRequestDtoString, CollectionRequestDto.class)
          .getCnote();
    } catch (IOException e) {
      log.error(
          "Could not parse collection request dto from {}. Error - ",
          collectionRequestDtoString,
          e);
    }
    return StringUtils.EMPTY_STRING;
  }

  /**
   * This function fetches pickup user and drs user and sends events.
   *
   * @param cnIdToConsignmentMap consignment id to consignment mapping
   * @param cnIdToPaymentDetailV2Map consignment id to pdv2 mapping
   */
  @Override
  public void syncExclusion(
      Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap,
      Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map) {
    if (MapUtils.isEmpty(cnIdToConsignmentMap)) {
      return;
    }
    final Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(cnIdToConsignmentMap.keySet());
    final Map<Long, Location> locationMap = locationService.getLocationMap();
    final Map<Long, String> prsToUser =
        getUserByPrsId(
            cnIdToConsignmentMap
                .values()
                .stream()
                .map(ConsignmentReadOnly::getPrsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    final Map<Long, String> drsToUser =
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
        prsToUser,
        drsToUser,
        ZoomEventType.HANDOVER_COLLECTION_EXCLUDE);
  }

  /**
   * This function fetches list of prs id to user mapping.
   *
   * @param prsIds list of prs ids.
   * @return map of prs id to user mapping.
   */
  private Map<Long, String> getUserByPrsId(List<Long> prsIds) {
    if (CollectionUtils.isEmpty(prsIds)) {
      return new HashMap<>();
    }
    final List<PickupRunSheet> pickupRunSheets = prsService.getPickupRunSheets(prsIds);
    return pickupRunSheets
        .stream()
        .collect(Collectors.toMap(PickupRunSheet::getId, prs -> prs.getUser().getEmail()));
  }

  /**
   * This function fetches list of drs id to user mapping.
   *
   * @param drsIds list of drs ids.
   * @return map of drs id to user mapping.
   */
  private Map<Long, String> getUserByDRSId(List<Long> drsIds) {
    if (CollectionUtils.isEmpty(drsIds)) {
      return new HashMap<>();
    }
    final Map<Long, Long> userIdByDrs =
        transportationPartnerMappingService.getUserIdByDrsId(drsIds);
    final Map<Long, String> userEmailMap = userMasterService.getUserEmailMap(userIdByDrs.values());
    final Map<Long, String> userByDsr = new ConcurrentHashMap<>();
    userIdByDrs.forEach((dsrId, userId) -> userByDsr.put(dsrId, userEmailMap.get(userId)));
    return userByDsr;
  }

  /**
   * This functions fetches cnIds, cnId to consignment read only mapping, list of payment detail v2
   * and consignment schedule.
   *
   * @param handoverCollectionEventPayload collections handover payload.
   * @param zoomEventType zoom event type.
   */
  @Override
  public void syncPostUnpost(
      HandoverCollectionEventPayload handoverCollectionEventPayload, ZoomEventType zoomEventType) {
    final List<Long> consignmentIds =
        consignmentDepositSlipRepository.findConsignmentIdByDepositSlipId(
            handoverCollectionEventPayload.getDepositSlipId());
    final Map<Long, ConsignmentReadOnly> consignmentMap =
        consignmentReadOnlyService.getConsignmentMap(consignmentIds);
    final List<PaymentDetailV2> paymentDetails =
        paymentDetailV2Service.getByConsignmentIdIn(consignmentIds);
    final Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(consignmentIds);
    final Map<Long, Location> locationMap = locationService.getLocationMap();

    buildRequestDtoAndSendEvents(
        paymentDetails,
        consignmentMap,
        consignmentScheduleMap,
        locationMap,
        Collections.emptyMap(),
        Collections.emptyMap(),
        zoomEventType);
  }

  /**
   * This function builds {@link CollectionRequestDto} and then sends events to transaction manager.
   *
   * @param paymentDetails list of payment detail v2.
   * @param consignmentMap consignment id to consignment read mapping.
   * @param consignmentScheduleMap consignment id to schedule mapping.
   * @param locationMap location id to location mapping.
   * @param prsToUser prs id to user mapping.
   * @param drsToUser drs id to user mapping.
   * @param zoomEventType zoom event type.
   */
  private void buildRequestDtoAndSendEvents(
      List<PaymentDetailV2> paymentDetails,
      Map<Long, ConsignmentReadOnly> consignmentMap,
      Map<Long, List<ConsignmentSchedule>> consignmentScheduleMap,
      Map<Long, Location> locationMap,
      Map<Long, String> prsToUser,
      Map<Long, String> drsToUser,
      ZoomEventType zoomEventType) {

    final List<CollectionRequestDto> collectionRequestDtos = new ArrayList<>();
    for (final PaymentDetailV2 paymentDetailV2 : paymentDetails) {
      ConsignmentReadOnly consignment = consignmentMap.get(paymentDetailV2.getConsignmentId());
      final CollectionRequestDto collectionRequestDto =
          CollectionRequestDto.builder()
              .consignmentId(consignment.getId())
              .cnote(consignment.getCnote())
              .eventType(getCollectionEventType(zoomEventType, paymentDetailV2.getPaymentType()))
              .amount(paymentDetailV2.getTotalRoundOffAmount())
              .bankTransferPendingApproval(paymentDetailV2.getBankName())
              .paymentMode(
                  com.rivigo.transaction.manager.client.enums.PaymentMode.valueOf(
                      paymentDetailV2.getPaymentMode().name()))
              .captainCode(getCaptainCode(consignment, paymentDetailV2, prsToUser, drsToUser))
              .build();
      setLocationDetails(
          collectionRequestDto, consignmentScheduleMap.get(consignment.getId()), locationMap);
      collectionRequestDtos.add(collectionRequestDto);
    }
    collectionRequestDtos.forEach(this::hitTransactionManagerAndLogResponse);
  }

  /**
   * This function returns captain code based on whether the consignment is PAID or TO-PAY.
   *
   * @param consignment consignment read only.
   * @param paymentDetailV2 payment detail v2.
   * @param prsToUser prs id to user mapping.
   * @param drsToUser drs id to user mapping.
   * @return captain code.
   */
  private static String getCaptainCode(
      ConsignmentReadOnly consignment,
      PaymentDetailV2 paymentDetailV2,
      Map<Long, String> prsToUser,
      Map<Long, String> drsToUser) {
    if (paymentDetailV2.getPaymentMode() == PaymentMode.PAID) {
      final Long prsId = consignment.getPrsId();
      return prsToUser.get(prsId);
    } else {
      final Long drsId = consignment.getDrsId();
      return drsToUser.get(drsId);
    }
  }

  /**
   * This function sets the delivery code and pickup code in {@link CollectionRequestDto}.
   *
   * @param collectionRequestDto collection request dto.
   * @param consignmentSchedules list of consignment schedules.
   * @param locationMap location id to location mapping.
   * @return enriched collection request dto.
   */
  private static CollectionRequestDto setLocationDetails(
      CollectionRequestDto collectionRequestDto,
      List<ConsignmentSchedule> consignmentSchedules,
      Map<Long, Location> locationMap) {
    final List<ConsignmentSchedule> filteredConsignmentSchedules =
        consignmentSchedules
            .stream()
            .filter(p -> LocationTypeV2.LOCATION == p.getLocationType())
            .filter(
                p -> !Arrays.asList(LocationTag.BF, LocationTag.DF).contains(p.getLocationTag()))
            .collect(Collectors.toList());

    filteredConsignmentSchedules
        .stream()
        .min(Comparator.comparing(ConsignmentSchedule::getSequence))
        .ifPresent(
            start ->
                collectionRequestDto.setPickupOuCode(
                    Optional.ofNullable(locationMap.get(start.getLocationId()))
                        .map(Location::getCode)
                        .orElse(null)));
    filteredConsignmentSchedules
        .stream()
        .max(Comparator.comparing(ConsignmentSchedule::getSequence))
        .ifPresent(
            end ->
                collectionRequestDto.setDeliveryOuCode(
                    Optional.ofNullable(locationMap.get(end.getLocationId()))
                        .map(Location::getCode)
                        .orElse(null)));
    return collectionRequestDto;
  }

  /**
   * This function returns collection event type based on zoom event type and payment type.
   *
   * @param zoomEventType zoom event type.
   * @param paymentType payment type.
   * @return collection event type.
   */
  private static CollectionEventType getCollectionEventType(
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
    }
    return CollectionEventType.CHEQUE_BOUNCE;
  }
}
