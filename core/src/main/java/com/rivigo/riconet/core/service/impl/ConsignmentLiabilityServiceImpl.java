package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.ConsignmentLiabilityService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.utils.FinanceUtils;
import com.rivigo.zoom.billing.dto.ConsignmentLiabilityPayload;
import com.rivigo.zoom.billing.enums.ConsignmentLiability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * updates ConsignmentLiability of CN
 *
 * @author Saurabh @Version 1 @Since 7 Dec 2020
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsignmentLiabilityServiceImpl implements ConsignmentLiabilityService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;
  private final ConsignmentService consignmentService;
  private final ObjectMapper objectMapper;

  /**
   * Hits Zoom Backend API to Update Consignment Liability
   *
   * @param payload of class ConsignmentLiabilityPayload
   */
  @Override
  public void updateConsignmentLiability(String payload) {
    log.info("Attempting liability update with payload {}", payload);
    ConsignmentLiabilityPayload consignmentLiabilityPayload =
        FinanceUtils.getDtoFromPayload(objectMapper, payload, ConsignmentLiabilityPayload.class);
    String cnote = consignmentLiabilityPayload.getCnote();
    Long consignmentId = consignmentService.getIdByCnote(cnote);
    ConsignmentLiability consignmentLiability =
        consignmentLiabilityPayload.getConsignmentLiability();
    zoomBackendAPIClientService.updateConsignmentLiability(consignmentId, consignmentLiability);
  }
}
