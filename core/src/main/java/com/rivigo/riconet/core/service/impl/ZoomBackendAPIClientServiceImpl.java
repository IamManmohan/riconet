package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
public class ZoomBackendAPIClientServiceImpl implements ZoomBackendAPIClientService {

  @Value("${zoom.url}")
  private String backendBaseUrl;

  @Autowired private ApiClientService apiClientService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Override
  public void setPriorityMapping(String cnote) {

    String url = UrlConstant.PRIORITY_URL_ENDPOINT;
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("cnote", Collections.singletonList(cnote));
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.PUT, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating priority mapping needed with consignmentId: {}", cnote, e);
      throw new ZoomException(
          "Error while updating priority mapping needed  with consignmentId: " + cnote);
    }

    apiClientService.parseJsonNode(responseJson, null);
  }

  @Override
  public void updateQcCheck(Long consignmentId, Boolean qcCheck) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("consignmentId", Collections.singletonList(consignmentId.toString()));
    valuesMap.put("qcCheck", Collections.singletonList(qcCheck.toString()));
    String url = UrlConstant.ZOOM_BACKEND_UPDATE_QC_CHECK;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.PUT, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error(
          "Error while marking consignment qcCheck needed with consignmentId: {}",
          consignmentId,
          e);
      throw new ZoomException(
          "Error while marking consignment qcCheck needed  with consignmentId: " + consignmentId);
    }
    apiClientService.parseJsonNode(responseJson, null);
  }

  @Override
  public void triggerPolicyGeneration(Long consignmentId) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    String url = UrlConstant.ZOOM_BACKEND_POLICY_GENERATION + consignmentId.toString();
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.POST, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error(
          "Error while triggering policy generation with consignmentId: {}", consignmentId, e);
      throw new ZoomException(
          "Error while triggering policy generation with consignmentId:  " + consignmentId);
    }
    apiClientService.parseJsonNode(responseJson, null);
  }

  @Override
  public void recalculateCpdOfBf(Long consignmentId) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("consignmentId", Collections.singletonList(consignmentId.toString()));
    String url = UrlConstant.ZOOM_BACKEND_BF_CPD_CALCULATION;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.PUT, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while recalculating cpd of BF consignment with id: {} ", consignmentId, e);
      throw new ZoomException(
          "Error while recalculating cpd of BF consignment with id: " + consignmentId);
    }
    apiClientService.parseJsonNode(responseJson, null);
  }

  @Override
  public ClientDTO addClient(ClientDTO clientDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE;
    log.info("Adding client {}", clientDTO);
    try {
      responseJson =
          apiClientService.getEntity(clientDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while creating Client {} , {}", clientDTO, e);
      throw new ZoomException("Error while creating Client {}" + clientDTO);
    }
    TypeReference<ClientDTO> mapType = new TypeReference<ClientDTO>() {};
    return (ClientDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public ClientDTO updateClient(ClientDTO clientDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE;
    log.info("Updating client {}", clientDTO);
    try {
      responseJson =
          apiClientService.getEntity(clientDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Client {} , {}", clientDTO, e);
      throw new ZoomException("Error while updating Client {}" + clientDTO);
    }
    TypeReference<ClientDTO> mapType = new TypeReference<ClientDTO>() {};
    return (ClientDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public ClientDTO deleteClient(Long id) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE + "/" + id.toString();
    log.info("Deleting client with id {}", id);
    try {
      responseJson = apiClientService.getEntity(null, HttpMethod.DELETE, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while deleting Client with Id {} , {}", id, e);
      throw new ZoomException("Error while deleting Client with Id {}" + id);
    }
    TypeReference<ClientDTO> mapType = new TypeReference<ClientDTO>() {};
    return (ClientDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public ConsignmentUploadedFilesDTO addInvoice(String invoiceUrl, String shortUrl, String cnote) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("shortUrl", Collections.singletonList(shortUrl));
    valuesMap.put("cnote", Collections.singletonList(cnote));
    valuesMap.put("url", Collections.singletonList(invoiceUrl));
    String url = UrlConstant.ZOOM_BACKEND_CONSIGNMENT_INVOICE;
    log.info("Updating invoice {} for cnote {}", shortUrl, cnote);
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.POST, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Invoice {} for Cnote {} , {}", shortUrl, cnote, e);
      throw new ZoomException("Error while updating Invoice for cnote :" + cnote);
    }
    TypeReference<ConsignmentUploadedFilesDTO> mapType =
        new TypeReference<ConsignmentUploadedFilesDTO>() {};
    try {
      return (ConsignmentUploadedFilesDTO) apiClientService.parseJsonNode(responseJson, mapType);
    } catch (Exception e) {
      log.error("Error converting json in dto , {}", e);
      return null;
    }
  }

  @Override
  public OrganizationDTO addOrganization(OrganizationDTO orgDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_ORGANIZATION_SERVICE;
    log.info("Adding organization {}", orgDTO);
    try {
      responseJson = apiClientService.getEntity(orgDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while creating Organization {} , {}", orgDTO, e);
      throw new ZoomException("Error while creating Organization {}" + orgDTO);
    }
    TypeReference<OrganizationDTO> mapType = new TypeReference<OrganizationDTO>() {};
    return (OrganizationDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public OrganizationDTO updateOrganization(OrganizationDTO orgDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_ORGANIZATION_SERVICE;
    log.info("Updating organization {}", orgDTO);
    try {
      responseJson = apiClientService.getEntity(orgDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Organization {} , {}", orgDTO, e);
      throw new ZoomException("Error while updating Organization {}" + orgDTO);
    }
    TypeReference<OrganizationDTO> mapType = new TypeReference<OrganizationDTO>() {};
    return (OrganizationDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }
}
