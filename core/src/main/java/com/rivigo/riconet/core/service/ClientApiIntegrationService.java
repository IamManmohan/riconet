package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.client.ClientIntegrationRequestDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import org.omg.PortableInterceptor.ClientRequestInfo;

import java.util.List;

public interface ClientApiIntegrationService {

  void getClientRequestDtosByType(NotificationDTO notificationDTO, String clientId);

  void publishEventsOfHiltiAndProcessErrors();

  void publishEventsOfFlipkartAndProcessErrors();
}
