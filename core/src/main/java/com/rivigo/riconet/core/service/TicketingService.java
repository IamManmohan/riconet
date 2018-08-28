package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
public interface TicketingService {

  void sendTicketingEventsEmail(NotificationDTO notificationDTO);
}
