package com.rivigo.riconet.core.service.impl;

import brave.internal.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeederVendorServiceImpl implements FeederVendorService {

  @Autowired private FeederVendorRepository feederVendorRepository;

  @Autowired private BusinessPartnerRepository businessPartnerRepository;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private FeederVendorService feederVendorService;

  @Override
  public FeederVendor getFeederVendorById(Long id) {
    return (feederVendorRepository.findById(id));
  }

  @Override
  public JsonNode createFeederVendor(String feederVendor) {
    VendorContractZoomEventDTO vendorContractZoomEventDTO =
        getVendorContractZoomEventDTOFromEventPayLoad(feederVendor);
    // based on the expense type we determine the data is for a vendor or a BP
    if (vendorContractZoomEventDTO.getExpenseType() != null) {
      switch (vendorContractZoomEventDTO.getExpenseType()) {
        case BP:
        case RP:
          return createBP(vendorContractZoomEventDTO);
        case RLH_FEEDER:
          return createVendor(vendorContractZoomEventDTO);
        default:
          throw new ZoomException(
              "Unknown vendor expense type: {}", vendorContractZoomEventDTO.getExpenseType());
      }
    } else {
      return null;
    }
  }

  @Nullable
  private JsonNode createVendor(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus(OperationalStatus.ACTIVE);
    dto.setLegalName(vendorContractZoomEventDTO.getLegalEntityName());
    Optional<FeederVendor> feederVendor =
        Optional.ofNullable(
            feederVendorRepository.findByVendorCode(vendorContractZoomEventDTO.getVendorCode()));
    if (feederVendor.isPresent()) {
      dto.setId(feederVendor.get().getId());
      log.info(
          "vendor details are already present with vendor code : {} ,id : {} ",
          feederVendor.get().getVendorCode(),
          feederVendor.get().getId());
      return null;
    }
    return zoomBackendAPIClientService.addFeederVendor(dto);
  }

  @Nullable
  private JsonNode createBP(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setType(vendorContractZoomEventDTO.getExpenseType().getName());
    dto.setLegalName(vendorContractZoomEventDTO.getLegalEntityName());
    dto.setStatus("ACTIVE");
    Optional<BusinessPartner> businessPartner =
        Optional.ofNullable(
            businessPartnerRepository.findByCode(vendorContractZoomEventDTO.getVendorCode()));
    if (businessPartner.isPresent()) {
      dto.setId(businessPartner.get().getId());
      log.info(
          "BP/RP details are already present with vendor code : {} ,id : {} ",
          businessPartner.get().getCode(),
          businessPartner.get().getId());
      return zoomBackendAPIClientService.addBusinessPartner(dto, HttpMethod.PUT);
    } else return zoomBackendAPIClientService.addBusinessPartner(dto, HttpMethod.POST);
  }

  @Nullable
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
}
