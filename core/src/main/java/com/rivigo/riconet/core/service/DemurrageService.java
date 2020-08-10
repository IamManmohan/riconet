package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface DemurrageService {

  void processEventToStartDemurrage(NotificationDTO notificationDTO);

  void processEventToEndDemurrage(NotificationDTO notificationDTO);
}
