package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;

public interface WriteOffService {

  void writeOff(String cnote, String actionValue, TicketDTO ticketDTO);
}
