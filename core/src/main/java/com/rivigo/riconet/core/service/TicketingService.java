package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
public interface TicketingService {

  void sendTicketingEventsEmail(NotificationDTO notificationDTO);

  void setPriorityMapping(NotificationDTO notificationDTO);

  void closeTicket(TicketDTO ticketDTO, String reasonOfClosure);

  TicketDTO getRequiredById(Long ticketId);

  void closeTicketIfRequired(TicketDTO ticketDTO, String actionClosureMessage);
}
