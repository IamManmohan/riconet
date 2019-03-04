package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.datastore.EwaybillMetadataDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomDatastoreAPIClientService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ZoomDatastoreAPIClientServiceImpl implements ZoomDatastoreAPIClientService {

  @Value("${zoom.datastore.url}")
  private String datastoreBaseUrl;

  private ApiClientService apiClientService;

  private ObjectMapper objectMapper;

  @Autowired
  public ZoomDatastoreAPIClientServiceImpl(
      ApiClientService apiClientService, ObjectMapper objectMapper) {
    this.apiClientService = apiClientService;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean cleanupAddressesUsingEwaybillMetadata(EwaybillMetadataDTO ewaybillMetadataDTO) {

    JsonNode responseJson;
    String url = UrlConstant.ZOOM_DATASTORE_EWAYBILL_METADATA_CLEANUP;

    try {
      responseJson =
          apiClientService.getEntity(
              ewaybillMetadataDTO, HttpMethod.POST, url, null, datastoreBaseUrl);
      return apiClientService.parseResponseJsonNodeFromDatastore(
          responseJson, objectMapper.constructType(Boolean.class));
    } catch (IOException e) {
      log.error(
          "Error while doing cleanup from ewaybill metadata from ewaybill {} : {}",
          ewaybillMetadataDTO.getEwaybillNumber(),
          e.getMessage());
    }

    return false;
  }
}
