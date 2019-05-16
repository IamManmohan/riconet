package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.enums.KairosMessageFieldNames;
import com.rivigo.riconet.core.service.ExpressAppPickupService;
import java.util.Map;

public class ExpressAppPickupServiceImpl implements ExpressAppPickupService {
  @Override
  public void processExpressPickupAutoCancellationEvent(Map<String, String> map) {
    String metadata = map.get(KairosMessageFieldNames.METADATA.name());
    if (metadata != null) {
      Long pickupId = Long.parseLong(metadata);
      // take action with this pickupId;
    }
  }
}
