package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.CLIENT_DEFAULT_SAM_ID;
import static com.rivigo.riconet.core.constants.ConsignmentConstant.GLOBAL_ORGANIZATION;
import static com.rivigo.riconet.core.constants.ConsignmentConstant.RETAIL_CLIENT_CODE;
import static com.rivigo.riconet.core.constants.ConsignmentConstant.RIVIGO_ORGANIZATION_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.ClientCreateUpdateDTO;
import com.rivigo.riconet.core.dto.EpodApplicableDto;
import com.rivigo.riconet.core.dto.client.BillingEntityDTO;
import com.rivigo.riconet.core.dto.client.ClientCodDodDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.dto.client.IndustryTypeDTO;
import com.rivigo.riconet.core.enums.ZoomServiceType;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.ClientVasDetailsService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.ClientVasType;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.BillingEntity;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.ClientVasDetail;
import com.rivigo.zoom.common.model.IndustryType;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.repository.mysql.BillingEntityRepository;
import com.rivigo.zoom.common.repository.mysql.ClientRepository;
import com.rivigo.zoom.common.repository.mysql.IndustryTypeRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class ClientMasterServiceImpl implements ClientMasterService {

  @Autowired ClientRepository clientRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserMasterService userMasterService;

  @Autowired private OrganizationService organizationService;

  @Autowired private IndustryTypeRepository industryTypeRepository;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private BillingEntityRepository billingEntityRepository;

  @Autowired private ClientVasDetailsService clientVasDetailsService;

  @Override
  public Client getClientById(Long id) {
    return clientRepository.findOne(id);
  }

  @Override
  public Client getClientByCode(String code) {
    return clientRepository.findByClientCode(code);
  }

  @Override
  public void createUpdateClient(String dtoString) {
    ClientCreateUpdateDTO clientCreateUpdateDTO = getClientCreateUpdateDTO(dtoString);
    if (clientCreateUpdateDTO == null) {
      return;
    }
    if (ZoomServiceType.ZOOM_FRANCHISE.name().equals(clientCreateUpdateDTO.getServiceType())) {
      log.info("Updating Organization {}", dtoString);
      organizationService.createUpdateOrganization(clientCreateUpdateDTO);
      return;
    }
    log.info("Updating Client {}", dtoString);
    Client existingClient =
        clientRepository.findByClientCode(clientCreateUpdateDTO.getClientCode());
    if (!clientCreateUpdateDTO.getActive()) {
      zoomBackendAPIClientService.deleteClient(existingClient.getId());
      return;
    }
    log.info("Existing client {}", existingClient);
    ClientDTO clientDTO = getClientDTOFromCreateUpdateDTO(clientCreateUpdateDTO);
    ClientDTO updatedClient;
    if (existingClient == null) {
      List<BillingEntityDTO> billingEntityDTOList = new ArrayList<>();
      clientCreateUpdateDTO
          .getBillingEntities()
          .stream()
          .forEach(
              be -> {
                BillingEntityDTO billingEntityDTO = new BillingEntityDTO();
                billingEntityDTO.setEntityName(be);
                billingEntityDTO.setIsActive(Boolean.TRUE);
                billingEntityDTOList.add(billingEntityDTO);
              });
      clientDTO.setBillingEntities(billingEntityDTOList);
      // rto_applicable is set to true in case of client addition
      clientDTO.setRtoApplicable(Boolean.TRUE);
      updatedClient = zoomBackendAPIClientService.addClient(clientDTO);
    } else {
      clientDTO.setId(existingClient.getId());
      clientDTO.setLaneRateBypass(existingClient.getLaneRateBypass());
      clientDTO.setOldClientCode(existingClient.getOldClientCode());
      clientDTO.setBillingEntities(
          getBillingEntityDTOList(
              clientCreateUpdateDTO.getBillingEntities(), existingClient.getId()));
      clientDTO.setRtoApplicable(existingClient.getRtoApplicable());
      updatedClient = zoomBackendAPIClientService.updateClient(clientDTO);
    }
    if (updatedClient != null) {
      createUpdateVasDetails(clientCreateUpdateDTO, updatedClient.getId());
    }
  }

  private ClientCreateUpdateDTO getClientCreateUpdateDTO(String dtoString) {
    ClientCreateUpdateDTO clientCreateUpdateDTO;
    try {
      clientCreateUpdateDTO = objectMapper.readValue(dtoString, ClientCreateUpdateDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", dtoString, ex);
      return null;
    }
    return clientCreateUpdateDTO;
  }

  private ClientDTO getClientDTOFromCreateUpdateDTO(ClientCreateUpdateDTO dto) {
    ClientDTO clientDTO = new ClientDTO();
    clientDTO.setClientCode(dto.getClientCode());
    clientDTO.setName(dto.getName());
    clientDTO.setDisplayName(dto.getDisplayName());
    clientDTO.setBillingName(dto.getBillingName());
    clientDTO.setBdm(dto.getBdm());
    clientDTO.setRbm(dto.getRbm());
    clientDTO.setCbm(dto.getCbm());
    clientDTO.setInsuranceReqd(dto.getInsuranceRequired());
    clientDTO.setFodApplicable(dto.getFodApplicable());
    clientDTO.setIsDacc(dto.getDaccApplicable());
    if (!CollectionUtils.isEmpty(dto.getNotificationToList())) {
      clientDTO.setNotificationToList(dto.getNotificationToList());
    } else {
      clientDTO.setNotificationToList(Collections.emptySet());
    }
    if (!CollectionUtils.isEmpty(dto.getNotificationCcList())) {
      clientDTO.setNotificationCcList(dto.getNotificationCcList());
    } else {
      clientDTO.setNotificationCcList(Collections.emptySet());
    }
    Set<String> clientNotificationList =
        Stream.concat(
                clientDTO.getNotificationToList().stream(),
                clientDTO.getNotificationCcList().stream())
            .collect(Collectors.toSet());
    if (!CollectionUtils.isEmpty(clientNotificationList)) {
      clientDTO.setClientNotificationList(clientNotificationList);
    } else {
      clientDTO.setClientNotificationList(Collections.emptySet());
    }
    clientDTO.setStatus(OperationalStatus.ACTIVE);
    clientDTO.setLaneRateBypass(Boolean.FALSE);
    clientDTO.setOldClientCode("--");
    if (clientDTO.getClientCode().equals(RETAIL_CLIENT_CODE)) {
      clientDTO.setOrganizationId(GLOBAL_ORGANIZATION);
    } else {
      clientDTO.setOrganizationId(RIVIGO_ORGANIZATION_ID);
    }
    if (ZoomServiceType.ZOOM_CORPORATE.name().equals(dto.getServiceType())
        || ZoomServiceType.RIVIGO_AIR.name().equals(dto.getServiceType())) {
      clientDTO.setCnoteType(CnoteType.NORMAL);
    } else if (dto.getServiceType().equals(ZoomServiceType.ZOOM_RETAIL.name())) {
      clientDTO.setCnoteType(CnoteType.RETAIL);
    }
    User samUser = userMasterService.getByEmail(dto.getSamUserEmail());
    if (samUser != null) {
      clientDTO.setSamUserId(samUser.getId());
    } else {
      clientDTO.setSamUserId(CLIENT_DEFAULT_SAM_ID);
    }
    User samLead = userMasterService.getByEmail(dto.getSamLeadEmail());
    if (samLead != null) {
      clientDTO.setSamLeadId(samLead.getId());
    } else {
      clientDTO.setSamLeadId(CLIENT_DEFAULT_SAM_ID);
    }
    if (!dto.getBillingEntities().isEmpty()) {
      clientDTO.setHasBillingEntity(Boolean.TRUE);
    } else {
      clientDTO.setHasBillingEntity(Boolean.FALSE);
    }
    IndustryType industryType = industryTypeRepository.findByType(dto.getIndustryType());
    IndustryTypeDTO industryTypeDTO = new IndustryTypeDTO();
    if (industryType != null) {
      industryTypeDTO.setId(industryType.getId());
      industryTypeDTO.setType(industryType.getType());
      industryTypeDTO.setContentTypes(industryType.getContentTypes());
    } else {
      industryTypeDTO.setType(dto.getIndustryType());
    }
    clientDTO.setIndustryType(industryTypeDTO);
    clientDTO.setIsOnlineEnabled(dto.getIsOODEnabled());
    return clientDTO;
  }

  private List<BillingEntityDTO> getBillingEntityDTOList(
      List<String> newBillingEntities, Long clientId) {
    List<BillingEntityDTO> billingEntityDTOList = new ArrayList<>();
    List<BillingEntity> billingEntities =
        billingEntityRepository.findAllByClientIdAndIsActive(clientId, Boolean.TRUE);
    List<String> billingEntityNames =
        billingEntities.stream().map(BillingEntity::getEntityName).collect(Collectors.toList());
    billingEntities
        .stream()
        .forEach(
            be -> {
              BillingEntityDTO billingEntityDTO = new BillingEntityDTO();
              billingEntityDTO.setId(be.getId());
              billingEntityDTO.setEntityName(be.getEntityName());
              billingEntityDTO.setClientId(clientId);
              if (newBillingEntities.contains(be.getEntityName())) {
                billingEntityDTO.setIsActive(Boolean.TRUE);
              } else {
                billingEntityDTO.setIsActive(Boolean.FALSE);
              }
              billingEntityDTOList.add(billingEntityDTO);
            });
    newBillingEntities
        .stream()
        .forEach(
            be -> {
              BillingEntityDTO billingEntityDTO = new BillingEntityDTO();
              if (!billingEntityNames.contains(be)) {
                billingEntityDTO.setEntityName(be);
                billingEntityDTO.setIsActive(Boolean.TRUE);
                billingEntityDTO.setClientId(clientId);
                billingEntityDTOList.add(billingEntityDTO);
              }
            });
    return billingEntityDTOList;
  }

  private void createUpdateVasDetails(ClientCreateUpdateDTO clientCreateUpdateDTO, Long clientId) {
    ClientVasDetail clientVasDetail = clientVasDetailsService.getClientVasDetails(clientId);
    if (!Optional.ofNullable(clientCreateUpdateDTO.getFinanceActivated()).orElse(false)
        && clientVasDetail == null) {
      log.info("Vas details not required for client");
      return;
    }
    ClientCodDodDTO clientVasDetailDTO = new ClientCodDodDTO();
    clientVasDetailDTO.setClientId(clientId);
    clientVasDetailDTO.setClientVasType(ClientVasType.COD_DOD);
    log.info("Saving cod dod details for {}", clientCreateUpdateDTO.toString());
    if (Optional.ofNullable(clientCreateUpdateDTO.getFinanceActivated()).orElse(false)) {
      clientVasDetailDTO.setDistrict(clientCreateUpdateDTO.getDistrict());
      clientVasDetailDTO.setInFavourOf(clientCreateUpdateDTO.getInFavorOf());
      clientVasDetailDTO.setLandmark(clientCreateUpdateDTO.getLandmark());
      clientVasDetailDTO.setPincode(clientCreateUpdateDTO.getPincode());
      clientVasDetailDTO.setState(clientCreateUpdateDTO.getState());
      clientVasDetailDTO.setMobileNumber(clientCreateUpdateDTO.getMobileNumber());
      clientVasDetailDTO.setStatus(OperationalStatus.ACTIVE.name());
      clientVasDetailDTO.setFinanceActivated(Boolean.TRUE);
    } else {
      clientVasDetailDTO.setStatus(OperationalStatus.INACTIVE.name());
      clientVasDetailDTO.setFinanceActivated(Boolean.FALSE);
    }
    if (clientVasDetail != null) {
      clientVasDetailDTO.setId(clientVasDetail.getId());
      zoomBackendAPIClientService.updateVasDetails(clientVasDetailDTO);
    } else {
      zoomBackendAPIClientService.addVasDetails(clientVasDetailDTO);
    }
  }
  /**
   * function that hits the zoom-backend for updating the e-pod applicable flag.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Override
  public void updateEpodDetails(String payload) {
    EpodApplicableDto epodApplicableDTO = getEpodApplicableDto(payload);
    zoomBackendAPIClientService.updateEpodDetails(epodApplicableDTO);
  }

  /**
   * function that coverts the dto String fetched from compass to EpodApplicableDto.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  private EpodApplicableDto getEpodApplicableDto(String dtoString) {
    EpodApplicableDto epodApplicableDTO;
    try {
      epodApplicableDTO = objectMapper.readValue(dtoString, EpodApplicableDto.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", dtoString, ex);
      return null;
    }
    return epodApplicableDTO;
  }
}
