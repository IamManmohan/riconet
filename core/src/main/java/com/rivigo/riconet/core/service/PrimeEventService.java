package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;

public interface PrimeEventService {

  void processEvent(PrimeEventDto primeEventDto);
}
