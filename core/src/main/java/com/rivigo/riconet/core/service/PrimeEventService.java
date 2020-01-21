package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.primesync.PrimeEventBaseDto;
import java.io.IOException;

public interface PrimeEventService {

  void processEvent(PrimeEventBaseDto primeEventBaseDto) throws IOException;
}
