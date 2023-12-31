package com.rivigo.riconet.core.service.impl;

import brave.internal.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.BusinessPartnerType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        case NLH:
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
    dto.setExpenseTypeList(Collections.singleton(vendorContractZoomEventDTO.getExpenseType()));
    Optional<FeederVendor> feederVendorOptional =
        Optional.ofNullable(
            feederVendorRepository.findByVendorCode(vendorContractZoomEventDTO.getVendorCode()));
    if (feederVendorOptional.isPresent()) {
      FeederVendor feederVendor = feederVendorOptional.get();
      dto.setId(feederVendor.getId());
      if (!CollectionUtils.isEmpty(feederVendor.getExpenseTypeList())
          && feederVendor
              .getExpenseTypeList()
              .contains(vendorContractZoomEventDTO.getExpenseType())) {
        // Update not allowed if feeder vendor entry already exists for given expense type.
        log.info(
            "vendor details are already present with vendor code : {} ,id : {}, expense type: {}",
            feederVendor.getVendorCode(),
            feederVendor.getId(),
            vendorContractZoomEventDTO.getExpenseType());
        return null;
      }
    }
    return zoomBackendAPIClientService.addFeederVendor(dto);
  }

  @Nullable
  private JsonNode createBP(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setType(BusinessPartnerType.VOVO.displayName());
    dto.setLegalName(vendorContractZoomEventDTO.getLegalEntityName());
    dto.setStatus(OperationalStatus.ACTIVE.toString());
    Optional<BusinessPartner> businessPartner =
        Optional.ofNullable(
            businessPartnerRepository.findByCode(vendorContractZoomEventDTO.getVendorCode()));
    if (businessPartner.isPresent()) {
      dto.setId(businessPartner.get().getId());
      log.info(
          "BP/RP details are already present with vendor code : {} ,id : {} ",
          businessPartner.get().getCode(),
          businessPartner.get().getId());
      return null;
    } else return zoomBackendAPIClientService.addBusinessPartner(dto);
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
