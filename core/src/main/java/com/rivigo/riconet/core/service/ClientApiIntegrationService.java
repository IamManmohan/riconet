package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.client.ClientIntegrationRequestDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import org.omg.PortableInterceptor.ClientRequestInfo;

import java.util.List;

public interface ClientApiIntegrationService {

  List<HiltiRequestDto> getHiltiRequestDtosByType(NotificationDTO notificationDTO);

  List<ClientIntegrationRequestDTO> getClientRequestDtosByType(NotificationDTO notificationDTO);

  void publishEventsOfHiltiAndProcessErrors();

  void publishEventsOfFlipkartAndProcessErrors();

  boolean addEventsToHiltiQueue(List<HiltiRequestDto> requestDto);

  boolean addEventsToFlipkartQueue(List<ClientIntegrationRequestDTO> requestDto);
}
