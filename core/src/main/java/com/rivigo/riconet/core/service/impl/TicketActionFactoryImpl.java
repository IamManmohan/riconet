package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ReasonConstant;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketActionDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.TicketActionFactory;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
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
        consignmentReadOnlyService.findConsignmentsByPickupId(Long.parseLong(pickupId));
    zoomTicketingAPIClientService
        .getTicketsByEntityInAndType(
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
        .forEach(zoomTicketingAPIClientService::performTicketAction);
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
        consumeHandoverTicketAction(ticketingService.getById(ticketId), entityId, actionValue);
        break;
      case ZoomTicketingConstant.PICKUP_BANK_TRANSFER_ACTION_NAME:
        consumePickupBankTransferAction(ticketingService.getById(ticketId), entityId, actionValue);
        break;
      case ZoomTicketingConstant.BANK_TRANSFER_ACTION_NAME:
        consumeBankTransferAction(ticketingService.getById(ticketId), entityId, actionValue);
        break;
      default:
        log.info("Action ignored since it is not related to Write Off - {}", actionName);
    }
  }

  private void consumeBankTransferAction(TicketDTO ticketDTO, String entityId, String actionValue) {
    // Validate ticket type
    if (!ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket {} is not bank-transfer ticket", ticketDTO.getId());
      return;
    }

    log.info("Initiating knock off for {}, request status : {}", entityId, actionValue);
    if (ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE.equals(actionValue)) {
      // knock off
      zoomBackendAPIClientService.handleKnockOffRequest(entityId);
    } else {
      // Add delivery blocker
      zoomBackendAPIClientService.handleConsignmentBlocker(
          ConsignmentBlockerRequestDTO.builder()
              .consignmentId(consignmentService.getIdByCnote(entityId))
              .requestType(ConsignmentBlockerRequestType.BLOCK)
              .reason(ReasonConstant.BANK_TRANSFER_BLOCKER_REASON)
              .subReason(ReasonConstant.BANK_TRANSFER_BLOCKER_SUB_REASON)
              .isActive(Boolean.TRUE)
              .build());
    }
  }
}
