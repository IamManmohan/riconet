package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.HandoverService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 23/11/18. */
@Slf4j
@Service
public class HandoverServiceImpl implements HandoverService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private final TicketingService ticketingService;

  @Autowired
  public HandoverServiceImpl(
      ZoomBackendAPIClientService zoomBackendAPIClientService, TicketingService ticketingService) {
    this.zoomBackendAPIClientService = zoomBackendAPIClientService;
    this.ticketingService = ticketingService;
  }

  @Override
  public void consumeHandoverTicketAction(
      Long ticketId, String cnote, String actionName, String actionValue) {
    if (ticketId == null) {
      return;
    }
    if (!ZoomTicketingConstant.WRITE_OFF_ACTION_NAME.equals(actionName)) {
      log.info("Action ignored since it is not related to Write Off");
      return;
    }
    TicketDTO ticketDTO = ticketingService.getTicketByTicketId(ticketId);
    if (ticketDTO == null) {
      throw new ZoomException("Error occured while fetching ticket {}", ticketId);
    }
    if (!ZoomTicketingConstant.WRITEOFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not write-off ticket");
      return;
    }
    WriteOffRequestAction writeOffRequestAction;
    if (ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE.equals(actionValue)) {
      writeOffRequestAction = WriteOffRequestAction.APPROVE;
    } else {
      writeOffRequestAction = WriteOffRequestAction.REJECT;
    }
    log.info("Initiating write off, reuest status : {}", writeOffRequestAction);
    zoomBackendAPIClientService.handleApproveRejectRequest(
        cnote, writeOffRequestAction);
    if (ticketDTO.getStatus() != TicketStatus.CLOSED) {
      log.info("Auto closing write-off ticket");
      ticketingService.closeTicket(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
    }
  }
}
