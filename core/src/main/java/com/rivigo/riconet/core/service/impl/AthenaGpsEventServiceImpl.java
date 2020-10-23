package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.athenagps.AthenaGpsEventDto;
import com.rivigo.riconet.core.service.AthenaGpsEventService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chiragbansal
 * @version 1
 * @since 21/10/20
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AthenaGpsEventServiceImpl implements AthenaGpsEventService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public void processEvent(AthenaGpsEventDto athenaGpsEventDto) {
    zoomBackendAPIClientService.processAthenaGpsEvent(athenaGpsEventDto);
  }
}
