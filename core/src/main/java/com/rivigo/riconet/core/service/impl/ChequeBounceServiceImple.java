package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomTicketing.TicketDTO;
import com.rivigo.riconet.core.service.ChequeBounceService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ashfakh on 8/5/18.
 */

@Service
@Slf4j
public class ChequeBounceServiceImple implements ChequeBounceService {

  @Autowired
  private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Override
  public void consumeChequeBounceEvent(NotificationDTO notificationDTO){
    TicketDTO ticketDTO = new TicketDTO();
    ticketDTO.setTypeId(ZoomTicketingConstant.RETAIL_CHEQUE_BOUNCE_TYPE_ID);
    ticketDTO.setTitle("");
    //TODO:fill details
    zoomTicketingAPIClientService.createTicket(ticketDTO);
  }

}
