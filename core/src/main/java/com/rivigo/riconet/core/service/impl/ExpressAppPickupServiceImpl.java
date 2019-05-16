package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.enums.CPBMessageFieldNames;
import com.rivigo.riconet.core.service.ExpressAppPickupService;
import java.util.Map;

public class ExpressAppPickupServiceImpl implements ExpressAppPickupService {
  @Override
  public void processExpressPickupEvent(Map<String, String> map) {
    String metadata = map.get(CPBMessageFieldNames.METADATA.name());
    if (metadata != null) {
      Long pickupId = Long.parseLong(metadata);
      // take action with this pickupId;
    }
  }
}
