package com.rivigo.riconet.event.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface ConsignmentBlockUnblockService {
  void processNotification(NotificationDTO notificationDTO);
}
