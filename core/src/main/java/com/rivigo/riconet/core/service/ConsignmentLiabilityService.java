package com.rivigo.riconet.core.service;

import org.springframework.stereotype.Service;

@Service
public interface ConsignmentLiabilityService {

  /**
   * Hits Zoom Backend API to Update Consignment Liability
   *
   * @param payload of class ConsignmentLiabilityPayload
   */
  void updateConsignmentLiability(String payload);
}
