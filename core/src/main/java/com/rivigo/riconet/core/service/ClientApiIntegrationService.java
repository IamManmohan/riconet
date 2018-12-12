package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface ClientApiIntegrationService {

  void getClientRequestDtosByType(NotificationDTO notificationDTO, String clientId);

  void publishEventsOfHiltiAndProcessErrors();

  void publishEventsOfFlipkartAndProcessErrors();
}
