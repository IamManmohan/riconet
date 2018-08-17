package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;

import java.util.List;

public interface HiltiApiService {

  List<HiltiRequestDto> getRequestDtosByType(NotificationDTO notificationDTO);

  void publishEventsAndProcessErrors();

  boolean addEventsToQueue(List<HiltiRequestDto> requestDto);

}
