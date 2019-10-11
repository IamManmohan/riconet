package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.WriteOffFactory;
import com.rivigo.riconet.core.service.WriteOffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WriteOffFactoryImpl implements WriteOffFactory {

  private final WriteOffService writeOffService;

  private final TicketingService ticketingService;

  private final ConsignmentReadOnlyService consignmentService;

  private void consumeHandoverTicketAction(TicketDTO ticketDTO, String cnote, String actionValue) {
    if (!ZoomTicketingConstant.WRITEOFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not write-off ticket");
      return;
    }
    writeOffService.writeOff(cnote, actionValue, ticketDTO);
    ticketingService.closeTicketIfRequired(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
  }

  private void consumePickupWriteOffAction(
      TicketDTO ticketDTO, String pickupId, String actionValue) {

    // Validate ticket type
    if (!ZoomTicketingConstant.PICKUP_WRITE_OFF_TYPE_ID.equals(ticketDTO.getTypeId())) {
      log.info("Ticket is not pickup write-off ticket");
      return;
    }

    // Move this to backend when writeoff API can accept multiple cnotes
    consignmentService
        .findConsignmentsByPickupId(Long.valueOf(pickupId))
        .forEach(cn -> writeOffService.writeOff(cn.getCnote(), actionValue, ticketDTO));

    ticketingService.closeTicketIfRequired(ticketDTO, ZoomTicketingConstant.ACTION_CLOSURE_MESSAGE);
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
      case ZoomTicketingConstant.PICKUP_WRITE_OFF_ACTION_NAME:
        consumePickupWriteOffAction(ticketingService.getById(ticketId), entityId, actionValue);
        break;
      default:
        log.info("Action ignored since it is not related to Write Off - {}", actionName);
    }
  }
}
