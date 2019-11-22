package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.collections.api.dto.HandoverCollectionExcludeEventPayload;
import com.rivigo.finance.utils.TimeUUID;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

  @Value("${retail.collection.dispute.topic}")
  private String retailCollectionDisputeTopic;

  @Override
  public void handleHandoverCollectionPostUnpostEvent(String payload) {
    // TODO : potential Error Handling

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
            .transactionType(getTransactionType(handoverCollectionEventPayload))
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
            // TODO clarify this - which date needs to go here (entry date or posting date)
            .effectedAt(handoverCollectionEventPayload.getCollectionPostingDate())
            .reference(String.valueOf(handoverCollectionEventPayload.getCollectionId()))
            .build();

    zoomBookAPIClientService.processZoomBookTransaction(
        Collections.singletonList(transactionRequestDTO));
  }

  private ZoomBookTransactionType getTransactionType(
      HandoverCollectionEventPayload handoverCollectionEventPayload) {
    switch (handoverCollectionEventPayload.getHandoverCollectionEventType()) {
      case COLLECTION_UNPOST:
        return ZoomBookTransactionType.DEBIT;
      case COLLECTION_EXCLUDE:
      case COLLECTION_POST:
      default:
        return ZoomBookTransactionType.CREDIT;
    }
  }

  @Override
  public void handleHandoverCollectionExcludeEvent(String payload) {
    // This is cheque bounce, and need to be handled as such

    // Parse this payload to HandoverCollectionEventPayload,
    // Get location dto for the location code,
    // create ZoomBookTransactionRequestDTO and hit the zoombook for creating transaction
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
        depositSlipService.findByDepositSlipNumber(handoverExcludePayload.getDepositSlipNumber());
    if (depositSlip == null) {
      throw new ZoomException(
          "DepositSlip not found - %s", handoverExcludePayload.getDepositSlipNumber());
    }

    // get all CNs from depositSlip
    final Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map = new HashMap<>();

    List<Long> consignmentIds =
        consignmentDepositSlipRepository.findConsignmentIdByDepositSlipId(depositSlip.getId());

    String chequeNumber = handoverExcludePayload.getChequeNumber();
    String bankName = handoverExcludePayload.getBankName();

    Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap =
        filterAllAffectedCNs(consignmentIds, cnIdToPaymentDetailV2Map, chequeNumber, bankName);

    // 2 entries, one credit, and one debit for cheque bounce
    transactionRequestDTO = getNewTransactionRequestDTOForChequeBounce(handoverExcludePayload);
    transactionRequestDTO.setTransactionType(ZoomBookTransactionType.DEBIT);
    transactionRequestDTOList.add(transactionRequestDTO);
    transactionRequestDTO = getNewTransactionRequestDTOForChequeBounce(handoverExcludePayload);
    transactionRequestDTO.setTransactionType(ZoomBookTransactionType.CREDIT);
    transactionRequestDTOList.add(transactionRequestDTO);

    // ZOOM BOOK API Call
    zoomBookAPIClientService.processZoomBookTransaction(transactionRequestDTOList);

    // Mark Recovery Pending API
    cnIdToConsignmentMap.forEach(
        (cnId, consignmentReadOnly) ->
            markRecoveryPending(
                consignmentReadOnly.getCnote(),
                chequeNumber,
                bankName,
                cnIdToPaymentDetailV2Map.get(cnId).getTotalAmount()));
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
        .remarks(payload.getRemarks().replaceAll("[^a-zA-Z0-9,./-]", " "))
        .effectedAt(payload.getCollectionPostingDate())
        .reference(
            payload.getDepositSlipNumber()
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
          PaymentDetailV2 byConsignmentId = paymentDetailV2Service.getByConsignmentId(cnId);
          // Filter by bankName and ChequeNumber
          if (byConsignmentId.getTransactionReferenceNo().equals(chequeNumber)
              && byConsignmentId.getBankName().equals(bankName))
            cnIdToPaymentDetailV2Map.put(cnId, byConsignmentId);
        });

    return cnIdToPaymentDetailV2Map
        .keySet()
        .stream()
        .map(consignmentReadOnlyService::findRequiredById)
        .collect(Collectors.toMap(ConsignmentReadOnly::getId, cn -> cn));
  }

  private ZoomBookTransactionHeader getTransactionHeader(
      HandoverCollectionEventPayload paymentDetail) {
    ZoomBookTransactionHeader a = null;
    // TODO This needs to come in some form in the payload
    return a;
  }

  private String getTimeUUID() {
    return String.valueOf(TimeUUID.createUUID(System.currentTimeMillis()));
  }

  private void markRecoveryPending(
      String cnote, String chequeNumber, String bankName, BigDecimal amount) {
    ChequeBounceDTO chequeBounceDTO =
        ChequeBounceDTO.builder()
            .cnote(cnote)
            .chequeNumber(chequeNumber)
            .bankName(bankName)
            .amount(amount)
            .build();
    JsonNode jsonNode = zoomBackendAPIClientService.markRecoveryPending(chequeBounceDTO);
    // Does this make a difference what happens to the request?
  }
}
