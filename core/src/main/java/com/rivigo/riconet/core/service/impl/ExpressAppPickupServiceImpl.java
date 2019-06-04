package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.enums.KairosMessageFieldNames;
import com.rivigo.riconet.core.service.ExpressAppPickupService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpressAppPickupServiceImpl implements ExpressAppPickupService {

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public void processExpressPickupAutoCancellationEvent(Map<String, String> map) {
    String metadata = map.get(KairosMessageFieldNames.METADATA.name());
    if (metadata != null) {
      Long pickupId = Long.parseLong(metadata);
      zoomBackendAPIClientService.deletePickup(pickupId);
    }
  }
}
