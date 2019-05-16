package com.rivigo.riconet.core.service;

import java.util.Map;

public interface ExpressAppPickupService {

  void processExpressPickupAutoCancellationEvent(Map<String, String> map);
}
