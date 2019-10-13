package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.WriteOffFactory;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WriteOffFactoryImpl implements WriteOffFactory {

  private final TicketingService ticketingService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private void consumeHandoverTicketAction(
      TicketDTO ticketDTO, String cnote, WriteOffRequestAction writeOffRequestAction) {
    if (!ZoomTicketingConstant.WRITEOFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not write-off ticket");
      return;
    }
    log.info("Initiating write off for {}, request status : {}", cnote, writeOffRequestAction);
    zoomBackendAPIClientService.handleApproveRejectRequest(cnote, writeOffRequestAction);
    ticketingService.closeTicketIfRequired(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
  }

  private void consumePickupWriteOffAction(
      TicketDTO ticketDTO, String pickupId, WriteOffRequestAction writeOffRequestAction) {

    // Validate ticket type
    if (!ZoomTicketingConstant.PICKUP_WRITE_OFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not pickup write-off ticket");
      return;
    }

    log.info(
        "Initiating pickup write off for {}, request status : {}", pickupId, writeOffRequestAction);
    zoomBackendAPIClientService.handlePickupWriteOffApproveRejectRequest(
        pickupId, writeOffRequestAction);
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
            ticketingService.getById(ticketId),
            entityId,
            getWriteOffRequestActionFromTicketAction(actionValue));
        break;
      case ZoomTicketingConstant.PICKUP_WRITE_OFF_ACTION_NAME:
        consumePickupWriteOffAction(
            ticketingService.getById(ticketId),
            entityId,
            getWriteOffRequestActionFromTicketAction(actionValue));
        break;
      default:
        log.info("Action ignored since it is not related to Write Off - {}", actionName);
    }
  }
}
