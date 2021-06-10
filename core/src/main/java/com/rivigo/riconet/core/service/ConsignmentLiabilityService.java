package com.rivigo.riconet.core.service;

/**
 * updates ConsignmentLiability of CN
 *
 * @author Saurabh @Version 1 @Since 7 Dec 2020
 */
public interface ConsignmentLiabilityService {

  /**
   * Hits Zoom Backend API to Update Consignment Liability
   *
   * @param payload of class ConsignmentLiabilityPayload
   */
  void updateConsignmentLiability(String payload);
}
