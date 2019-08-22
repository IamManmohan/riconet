package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeederVendorServiceImpl implements FeederVendorService {

  @Autowired FeederVendorRepository feederVendorRepository;

  @Autowired BusinessPartnerRepository businessPartnerRepository;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private FeederVendorService feederVendorService;

  @Override
  public FeederVendor getFeederVendorById(Long id) {
    return (feederVendorRepository.findById(id));
  }

  @Override
  public void createFeederVendor(String feederVendor) {
    VendorContractZoomEventDTO vendorContractZoomEventDTO =
        getVendorContractZoomEventDTOFromEventPayLoad(feederVendor);
    // based on the expense type we determine the data is for a vendor or a BP
    switch (vendorContractZoomEventDTO.getExpenseType()) {
      case BP:
      case RP:
        createBP(vendorContractZoomEventDTO);
        break;
      case RLH_FEEDER:
        createVendor(vendorContractZoomEventDTO);
        break;
    }
  }

  private void createVendor(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus("ACTIVE");
    dto.setLegalName(vendorContractZoomEventDTO.getLegalEntityName());
    FeederVendor feederVendor =
        Optional.ofNullable(
                feederVendorRepository.findByVendorCode(vendorContractZoomEventDTO.getVendorCode()))
            .orElse(null);
    if (feederVendor != null) {
      dto.setId(feederVendor.getId());
      log.info("vendor details are already present");
    } else zoomBackendAPIClientService.addUpdateFeederVendor(dto, HttpMethod.POST);
  }

  private void createBP(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setType(vendorContractZoomEventDTO.getExpenseType().getName());
    dto.setLegalName(vendorContractZoomEventDTO.getLegalEntityName());
    dto.setStatus("ACTIVE");
    BusinessPartner businessPartner =
        Optional.ofNullable(
                businessPartnerRepository.findByCode(vendorContractZoomEventDTO.getVendorCode()))
            .orElse(null);
    if (businessPartner != null) {
      dto.setId(businessPartner.getId());
      log.info("BP/RP details are already present");
    } else zoomBackendAPIClientService.addUpdateBusinessPartner(dto);
  }

  private VendorContractZoomEventDTO getVendorContractZoomEventDTOFromEventPayLoad(
      String feederVendor) {
    VendorContractZoomEventDTO vendorContractZoomEventDTO;
    try {
      vendorContractZoomEventDTO =
          objectMapper.readValue(feederVendor, VendorContractZoomEventDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", feederVendor, ex);
      return null;
    }
    return vendorContractZoomEventDTO;
  }

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
