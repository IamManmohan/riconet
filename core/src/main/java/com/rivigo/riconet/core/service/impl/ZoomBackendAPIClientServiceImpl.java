package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
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
}
