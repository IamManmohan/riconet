package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.BankTransferRequestDTO;
import com.rivigo.riconet.core.dto.ChequeBounceDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketActionDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.TicketActionFactory;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketActionFactoryImpl implements TicketActionFactory {

  private final TicketingService ticketingService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  private final ConsignmentService consignmentService;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  private final PaymentDetailV2Service paymentDetailV2Service;

  private void consumeHandoverTicketAction(
      TicketDTO ticketDTO, String cnote, String requestAction) {
    if (!ZoomTicketingConstant.WRITEOFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not write-off ticket");
      return;
    }
    WriteOffRequestAction writeOffRequestAction =
        getWriteOffRequestActionFromTicketAction(requestAction);
    log.info("Initiating write off for {}, request status : {}", cnote, writeOffRequestAction);
    zoomBackendAPIClientService.handleWriteOffApproveRejectRequest(cnote, writeOffRequestAction);
    ticketingService.closeTicketIfRequired(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
  }

  private void consumePickupBankTransferAction(
      TicketDTO ticketDTO, String pickupId, String actionValue) {

    // Validate ticket type
    if (!ZoomTicketingConstant.PICKUP_BANK_TRANSFER_TICKET_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket {} is not pickup bank-transfer ticket", ticketDTO.getId());
      return;
    }

    log.info(
        "Performing action on all child tickets for pickup: {}, request status : {}",
        pickupId,
        actionValue);
    List<ConsignmentReadOnly> consignments =
        consignmentReadOnlyService.findByPickupId(Long.parseLong(pickupId));
    zoomTicketingAPIClientService
        .getByEntityInAndType(
            consignments.stream().map(ConsignmentReadOnly::getCnote).collect(Collectors.toList()),
            ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID.toString())
        .stream()
        .map(
            t ->
                TicketActionDTO.builder()
                    .actionName(ZoomTicketingConstant.BANK_TRANSFER_ACTION_NAME)
                    .actionValue(actionValue)
                    .ticketId(t.getId())
                    .build())
        .forEach(zoomTicketingAPIClientService::performAction);
    ticketingService.closeTicketIfRequired(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
  }

  private WriteOffRequestAction getWriteOffRequestActionFromTicketAction(String actionValue) {
    WriteOffRequestAction writeOffRequestAction;
    if (ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE.equals(actionValue)) {
      writeOffRequestAction = WriteOffRequestAction.APPROVE;
    } else {
      writeOffRequestAction = WriteOffRequestAction.REJECT;
    }
    return writeOffRequestAction;
  }

  @Override
  public void consume(Long ticketId, String entityId, String actionName, String actionValue) {
    if (ticketId == null) {
      return;
    }
    switch (String.valueOf(actionName)) {
      case ZoomTicketingConstant.WRITE_OFF_ACTION_NAME:
        consumeHandoverTicketAction(
            ticketingService.getRequiredById(ticketId), entityId, actionValue);
        break;
      case ZoomTicketingConstant.PICKUP_BANK_TRANSFER_ACTION_NAME:
        consumePickupBankTransferAction(
            ticketingService.getRequiredById(ticketId), entityId, actionValue);
        break;
      case ZoomTicketingConstant.BANK_TRANSFER_ACTION_NAME:
        consumeBankTransferAction(
            ticketingService.getRequiredById(ticketId), entityId, actionValue);
        break;
      default:
        log.info("Action ignored since it is not related to Write Off - {}", actionName);
    }
  }

  private void consumeBankTransferAction(TicketDTO ticketDTO, String cnote, String actionValue) {
    // Validate ticket type
    if (!ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket {} is not bank-transfer ticket", ticketDTO.getId());
      return;
    }

    PaymentDetailV2 paymentDetailV2 =
        paymentDetailV2Service.getByConsignmentId(consignmentService.getIdByCnote(cnote));

    if (paymentDetailV2.getPaymentType() != PaymentType.BANK_TRANSFER) {
      log.info(
          "Current payment type for cn: {} is not bank transfer. Will not trigger auto knock-off/recovery",
          cnote);
      return;
    }

    log.info("Initiating knock off for {}, request status : {}", cnote, actionValue);
    if (ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE.equals(actionValue)) {
      // knock off
      zoomBackendAPIClientService.handleKnockOffRequest(
          cnote,
          new BankTransferRequestDTO(
              paymentDetailV2.getBankAccountReference(),
              paymentDetailV2.getTransactionReferenceNo()));
    } else {
      // Mark recovery
      zoomBackendAPIClientService.markRecoveryPending(
          ChequeBounceDTO.builder()
              .amount(paymentDetailV2.getTotalAmount())
              .bankName(paymentDetailV2.getBankName())
              .bankAccountReference(paymentDetailV2.getBankAccountReference())
              .chequeNumber(paymentDetailV2.getTransactionReferenceNo())
              .cnote(cnote)
              .build());
    }
  }
}
