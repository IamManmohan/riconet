package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.service.WriteOffService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WriteOffServiceImpl implements WriteOffService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public void writeOff(String cnote, String actionValue, TicketDTO ticketDTO) {
    WriteOffRequestAction writeOffRequestAction;
    if (ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE.equals(actionValue)) {
      writeOffRequestAction = WriteOffRequestAction.APPROVE;
    } else {
      writeOffRequestAction = WriteOffRequestAction.REJECT;
    }
    log.info("Initiating write off for {}, request status : {}", cnote, writeOffRequestAction);
    zoomBackendAPIClientService.handleApproveRejectRequest(cnote, writeOffRequestAction);
  }
}
