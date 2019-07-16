package com.rivigo.riconet.core.service.impl;

import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.VendorOnboardingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VendorOnboardingServiceImpl implements VendorOnboardingService {

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;
  @Autowired private ClientMasterService clientMasterService;
  @Autowired private FeederVendorService feederVendorService;

  @Override
  public void processVendorOnboardingEvent(EventPayload eventPayload) {
    ZoomEventType eventType = eventPayload.getEventType();
    switch (eventType) {
      case VENDOR_ACTIVE_EVENT:
        feederVendorService.createFeederVendor(eventPayload.getPayload());
        break;
      default:
        log.info("Event does not trigger anything {}", eventType);
    }
  }
}
