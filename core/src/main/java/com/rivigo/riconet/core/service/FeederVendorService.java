package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.zoom.common.model.FeederVendor;
import org.springframework.stereotype.Service;

@Service
public interface FeederVendorService {

  FeederVendor getFeederVendorById(Long id);

  JsonNode createFeederVendor(String feederVendor);

  void processVendorOnboardingEvent(EventPayload eventPayload);
}
