package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.CLIENT_DEFAULT_SAM_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.ClientCreateUpdateDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.repository.mysql.OrganizationRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

  @Autowired private OrganizationRepository organizationRepository;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private UserMasterService userMasterService;

  @Autowired private ObjectMapper objectMapper;

  @Override
  public Organization getById(Long orgId) {
    return organizationRepository.findOne(orgId);
  }

  @Override
  public List<Organization> getByOrganizationTypeAndOperationalStatus(
      OrganizationType rivigo, OperationalStatus status) {
    return organizationRepository.findByTypeAndStatus(rivigo, status);
  }

  @Override
  public void createUpdateOrganization(ClientCreateUpdateDTO dto) {
    Organization existingOrg = organizationRepository.findByCode(dto.getClientCode());
    OrganizationDTO organizationDTO = getOrganizationDTO(dto);
    if (existingOrg != null) {
      organizationDTO.setId(existingOrg.getId());
      zoomBackendAPIClientService.updateOrganization(organizationDTO);
    } else {
      zoomBackendAPIClientService.addOrganization(organizationDTO);
    }
  }

  private OrganizationDTO getOrganizationDTO(ClientCreateUpdateDTO dto) {
    OrganizationDTO organizationDTO = new OrganizationDTO();
    organizationDTO.setCode(dto.getClientCode());
    organizationDTO.setName(dto.getName());
    organizationDTO.setType(OrganizationType.BF);
    if (dto.getActive()) {
      organizationDTO.setStatus(OperationalStatus.ACTIVE);
    } else {
      organizationDTO.setStatus(OperationalStatus.INACTIVE);
    }
    organizationDTO.setInsuranceApplicable(dto.getInsuranceRequired());
    organizationDTO.setFodApplicable(dto.getFodApplicable());
    organizationDTO.setEmail(dto.getClientEmail());
    organizationDTO.setMobileNumber(dto.getClientPhoneNumber());
    User samUser = userMasterService.getByEmail(dto.getSamUserEmail());
    if (samUser != null) {
      organizationDTO.setSamUserId(samUser.getId());
    } else {
      organizationDTO.setSamUserId(CLIENT_DEFAULT_SAM_ID);
    }
    User samLead = userMasterService.getByEmail(dto.getSamLeadEmail());
    if (samLead != null) {
      organizationDTO.setSamLeadId(samLead.getId());
    } else {
      organizationDTO.setSamLeadId(CLIENT_DEFAULT_SAM_ID);
    }
    organizationDTO.setDisableRetailBooking(Boolean.FALSE);
    return organizationDTO;
  }
}
