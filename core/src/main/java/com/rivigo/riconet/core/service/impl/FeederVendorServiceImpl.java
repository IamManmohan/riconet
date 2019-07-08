package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.BusinessPartnerConverter;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.ExpenseType;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.dto.VendorContractStatus;
import com.rivigo.riconet.core.dto.VendorContractZoomEventDTO;
import com.rivigo.riconet.core.service.BusinessPartnerService;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.rivigo.riconet.core.dto.ExpenseType.*;

@Slf4j
@Service
public class FeederVendorServiceImpl implements FeederVendorService {

  @Autowired FeederVendorRepository feederVendorRepository;

  @Autowired private FeederVendorConverter feederVendorConverter;

  @Autowired private BusinessPartnerService businessPartnerService;

  @Autowired private ObjectMapper objectMapper;

  @Override
  public FeederVendor getFeederVendorById(Long id) {
    return (feederVendorRepository.findById(id));
  }

  @Override
  public void createFeederVendor(String feederVendor) {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = getVendorContractZoomEventDTO(feederVendor);
    // based on the expense type we determine the data is for a vendor or a BP
    switch(vendorContractZoomEventDTO.getExpenseType()){
      case BP:
      case RP: createBP(vendorContractZoomEventDTO);
        break;
      case RLH_FEEDER: createVendor(vendorContractZoomEventDTO);
        break;

    }
  }

  private void createVendor(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    FeederVendor dto = new FeederVendor();
    dto.setVendorCode(vendorContractZoomEventDTO.getVendorCode());
    dto.setVendorType(FeederVendor.VendorType.VENDOR);

    if(vendorContractZoomEventDTO.getExpenseType().equals(VendorContractStatus.ACTIVE)) {
      dto.setVendorStatus(OperationalStatus.ACTIVE);
    }else{
      dto.setVendorStatus(OperationalStatus.INACTIVE);
    }
    feederVendorConverter.convertFrom(feederVendorRepository.save(dto));
  }

  private void createBP(VendorContractZoomEventDTO vendorContractZoomEventDTO) {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode(vendorContractZoomEventDTO.getVendorCode());
    if(vendorContractZoomEventDTO.getExpenseType().equals(VendorContractStatus.ACTIVE)) {
      dto.setStatus(OperationalStatus.ACTIVE.toString());
    }else{
      dto.setStatus(OperationalStatus.INACTIVE.toString());
    }
    businessPartnerService.addBusinessPartner(dto);
  }

  private VendorContractZoomEventDTO getVendorContractZoomEventDTO(String feederVendor) {
    VendorContractZoomEventDTO vendorContractZoomEventDTO;
    try {
      vendorContractZoomEventDTO = objectMapper.readValue(feederVendor, VendorContractZoomEventDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", feederVendor, ex);
      return null;
    }
    return vendorContractZoomEventDTO;
  }
}
