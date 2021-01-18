package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.athenagps.AthenaGpsEventDto;

public interface AthenaGpsEventService {

  void processEvent(AthenaGpsEventDto athenaGpsEventDto);
}
