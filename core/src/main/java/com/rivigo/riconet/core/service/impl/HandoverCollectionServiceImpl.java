package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.collections.api.dto.HandoverCollectionExcludeEventPayload;
import com.rivigo.finance.utils.TimeUUID;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.constants.HandoverConstant;
import com.rivigo.riconet.core.dto.ChequeBounceDTO;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.DepositSlipService;
import com.rivigo.riconet.core.service.HandoverCollectionService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomBookAPIClientService;
import com.rivigo.zoom.common.dto.zoombook.ZoomBookTransactionRequestDTO;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookFunctionType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTenantType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionHeader;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionSubHeader;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.depositslip.ConsignmentDepositSlipRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class HandoverCollectionServiceImpl implements HandoverCollectionService {

  private final ObjectMapper objectMapper;

  private final ZoomBookAPIClientService zoomBookAPIClientService;

  private final LocationService locationService;

  private final DepositSlipService depositSlipService;

  private final ConsignmentDepositSlipRepository consignmentDepositSlipRepository;

  private final PaymentDetailV2Service paymentDetailV2Service;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  @Override
  public void handleHandoverCollectionPostUnpostEvent(String payload, ZoomEventType eventType) {
    // Parse this payload to HandoverCollectionEventPayload,
    // Get location dto for the location code,
    // create ZoomBookTransactionRequestDTO and hit the zoombook for creating transaction
    HandoverCollectionEventPayload handoverCollectionEventPayload = null;
    try {
      handoverCollectionEventPayload =
          objectMapper.readValue(payload, HandoverCollectionEventPayload.class);
    } catch (IOException e) {
      log.error("Failed to parse HandoverCollectionEventPayload - {}", e.getLocalizedMessage());
      throw new ZoomException("Failed to create DTO from payload - %s", payload);
    }

    // fill transactionRequestDTO
    ZoomBookTransactionRequestDTO transactionRequestDTO =
        ZoomBookTransactionRequestDTO.builder()
            .transactionType(getTransactionType(handoverCollectionEventPayload, eventType))
            .clientRequestId(getTimeUUID())
            .tenantType(ZoomBookTenantType.RETAIL)
            .functionType(ZoomBookFunctionType.OU_OUTSTANDING)
            .orgId(getLocation(handoverCollectionEventPayload.getLocationCode()).getId())
            .amount(handoverCollectionEventPayload.getCollectionAmount())
            .transactionHeader(
                getTransactionHeader(handoverCollectionEventPayload)) // (CASH OR CHEQUE)
            .transactionSubHeader(ZoomBookTransactionSubHeader.KNOCKOFF)
            .remarks(payload)
            // .notification() //not needed
            .effectedAt(handoverCollectionEventPayload.getCollectionEntryDate())
            .reference(String.valueOf(handoverCollectionEventPayload.getCollectionId()))
            .build();

    zoomBookAPIClientService.processZoomBookTransaction(
        Collections.singletonList(transactionRequestDTO));
  }

  private ZoomBookTransactionType getTransactionType(
      HandoverCollectionEventPayload handoverCollectionEventPayload, ZoomEventType eventType) {
    switch (eventType) {
      case HANDOVER_COLLECTION_UNPOST:
        return ZoomBookTransactionType.DEBIT;
      case HANDOVER_COLLECTION_EXCLUDE:
      case HANDOVER_COLLECTION_POST:
      default:
        return ZoomBookTransactionType.CREDIT;
    }
  }

  @Override
  public void handleHandoverCollectionExcludeEvent(String payload, ZoomEventType eventType) {
    // This is cheque bounce, and need to be handled as such

    // Parse this payload to HandoverCollectionEventPayload,
    // Get location dto for the location code,
    // create ZoomBookTransactionRequestDTO and hit the zoombook for creating transaction
    log.info("Handling Cheque Bounce Event from collections service");
    List<ZoomBookTransactionRequestDTO> transactionRequestDTOList = new ArrayList<>();
    ZoomBookTransactionRequestDTO transactionRequestDTO;

    HandoverCollectionExcludeEventPayload handoverExcludePayload;
    try {
      handoverExcludePayload =
          objectMapper.readValue(payload, HandoverCollectionExcludeEventPayload.class);
    } catch (IOException e) {
      log.error(
          "Failed to parse HandoverCollectionExcludeEventPayload - {}", e.getLocalizedMessage());
      throw new ZoomException("Failed to create DTO from payload - %s", payload);
    }

    DepositSlip depositSlip =
        depositSlipService
            .findByDepositSlipId(handoverExcludePayload.getDepositSlipId())
            .orElseThrow(
                () ->
                    new ZoomException(
                        "DepositSlip not found - %s", handoverExcludePayload.getDepositSlipId()));

    // get all CNs from depositSlip
    final Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map = new HashMap<>();

    List<Long> consignmentIds =
        consignmentDepositSlipRepository.findConsignmentIdByDepositSlipId(depositSlip.getId());

    String chequeNumber = handoverExcludePayload.getChequeNumber();
    String bankName = handoverExcludePayload.getBankName();

    Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap =
        filterAllAffectedCNs(consignmentIds, cnIdToPaymentDetailV2Map, chequeNumber, bankName);

    if (MapUtils.isEmpty(cnIdToConsignmentMap)) {
      log.info(
          "Did not find any affected CNs, skipping all further actions for event {}, and payload: {}",
          eventType.name(),
          payload);
      return;
    }

    // 2 entries, one credit, and one debit for cheque bounce
    transactionRequestDTO = getNewTransactionRequestDTOForChequeBounce(handoverExcludePayload);
    transactionRequestDTO.setTransactionType(ZoomBookTransactionType.DEBIT);
    transactionRequestDTOList.add(transactionRequestDTO);
    transactionRequestDTO = SerializationUtils.clone(transactionRequestDTO);
    transactionRequestDTO.setTransactionType(ZoomBookTransactionType.CREDIT);
    transactionRequestDTO.setClientRequestId(getTimeUUID());
    transactionRequestDTOList.add(transactionRequestDTO);

    log.info(
        "Making zoomBook API call for creating transactions, transactionsList: {}",
        transactionRequestDTOList);
    // ZOOM BOOK API Call
    zoomBookAPIClientService.processZoomBookTransaction(transactionRequestDTOList);

    // Mark Recovery Pending API
    List<ChequeBounceDTO> chequeBounceDTOListForRecoveryPendingAPI = new ArrayList<>();
    cnIdToConsignmentMap.forEach(
        (key, consignmentReadOnly) -> {
          PaymentDetailV2 paymentDetailV2 = cnIdToPaymentDetailV2Map.get(key);
          ChequeBounceDTO oneDto =
              ChequeBounceDTO.builder()
                  .cnote(consignmentReadOnly.getCnote())
                  .chequeNumber(chequeNumber)
                  .bankName(bankName)
                  .amount(paymentDetailV2.getTotalAmount())
                  .bankAccountReference(paymentDetailV2.getBankAccountReference())
                  .build();
          chequeBounceDTOListForRecoveryPendingAPI.add(oneDto);
        });
    log.info(
        "Making zoom backend markRecoveryPendingV2 API calls for payload: {}",
        chequeBounceDTOListForRecoveryPendingAPI);
    markRecoveryPending(chequeBounceDTOListForRecoveryPendingAPI);
  }

  private Location getLocation(String code) {
    return locationService.getLocationByCode(code);
  }

  private ZoomBookTransactionRequestDTO getNewTransactionRequestDTOForChequeBounce(
      HandoverCollectionExcludeEventPayload payload) {
    return ZoomBookTransactionRequestDTO.builder()
        .clientRequestId(getTimeUUID())
        .tenantType(ZoomBookTenantType.RETAIL)
        .functionType(ZoomBookFunctionType.OU_OUTSTANDING)
        .orgId(getLocation(payload.getLocationCode()).getId())
        .amount(payload.getCollectionAmount())
        .transactionHeader(ZoomBookTransactionHeader.CHEQUE)
        .transactionSubHeader(ZoomBookTransactionSubHeader.BOUNCED)
        .remarks(
            Optional.ofNullable(payload.getRemarks())
                .orElse("")
                .replaceAll("[^a-zA-Z0-9,./-]", " "))
        .effectedAt(payload.getCollectionPostingDate())
        .reference(
            payload.getDepositSlipId()
                + "|"
                + payload.getBankName()
                + "|"
                + payload.getChequeNumber())
        .build();
  }

  // WARNING: MUTATOR : Fills up the paymentDetailsV2List as well
  private Map<Long, ConsignmentReadOnly> filterAllAffectedCNs(
      List<Long> consignmentIDs,
      Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map,
      String chequeNumber,
      String bankName) {

    consignmentIDs.forEach(
        cnId -> {
          PaymentDetailV2 paymentDetail = paymentDetailV2Service.getByConsignmentId(cnId);
          // Filter by bankName and ChequeNumber and Handover Status(any pending are not needed)
          if (paymentDetail != null
              && Objects.equals(chequeNumber, paymentDetail.getTransactionReferenceNo())
              && Objects.equals(bankName, paymentDetail.getBankName())
              && !HandoverConstant.pendingStatuses.contains(paymentDetail.getHandoverStatus())) {
            cnIdToPaymentDetailV2Map.put(cnId, paymentDetail);
          }
        });

    return cnIdToPaymentDetailV2Map
        .keySet()
        .stream()
        .map(consignmentReadOnlyService::findRequiredById)
        .collect(Collectors.toMap(ConsignmentReadOnly::getId, cn -> cn));
  }

  private ZoomBookTransactionHeader getTransactionHeader(
      HandoverCollectionEventPayload handoverCollectionEventPayload) {
    ZoomBookTransactionHeader transactionHeader = null;
    if (handoverCollectionEventPayload.getPaymentType() == null) {
      throw new ZoomException("Invalid PaymentType received, %s", handoverCollectionEventPayload);
    }
    switch (handoverCollectionEventPayload.getPaymentType()) {
      case CHEQUE:
        transactionHeader = ZoomBookTransactionHeader.CHEQUE;
        break;
      case CASH:
        transactionHeader = ZoomBookTransactionHeader.CASH;
        break;
      default:
        // Currently it can either be cash or cheque, so lets just keep them at that.
        // Online knockoffs are not supposed to come from this event(compass collection service).
        throw new ZoomException("Invalid PaymentType received, %s", handoverCollectionEventPayload);
    }
    return transactionHeader;
  }

  private String getTimeUUID() {
    return String.valueOf(TimeUUID.createUUID(System.currentTimeMillis()));
  }

  private void markRecoveryPending(List<ChequeBounceDTO> chequeBounceDTOList) {
    JsonNode jsonNode = zoomBackendAPIClientService.markRecoveryPendingV2(chequeBounceDTOList);
    log.info(
        "API call for chequeBounceEvent done, for payload: {}, response: {}",
        chequeBounceDTOList,
        jsonNode);
    // Does this make a difference what happens to the request?
  }
}
