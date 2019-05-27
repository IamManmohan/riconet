package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface RTOService {

  void validateAndCreateRTOForwardTask(NotificationDTO notificationDTO);

  void reassignRTOTicketIfExists(NotificationDTO notificationDTO);

  void processTaskClosedEvent(NotificationDTO notificationDTO);
}
