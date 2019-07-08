package com.rivigo.riconet.core.service;

import com.rivigo.finance.zoom.dto.EventPayload;

public interface VendorOnboardingService {

  void processVendorOnboardingEvent(EventPayload eventPayload);
}
