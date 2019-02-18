package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.UrlConstant.QC_MODEL_GET_FLAG;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.qc.QcRequestDTO;
import com.rivigo.riconet.core.dto.qc.QcResponseDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.QcApiClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 11/02/19. */
@Slf4j
@Service
public class QcApiClientServiceImpl implements QcApiClientService {

  @Autowired private ObjectMapper objectMapper;

  @Value("${zoom.qc.url}")
  private String qcBaseUrl;

  @Autowired private ApiClientService apiClientService;

  @Override
  public QcResponseDTO getQcFlag(QcRequestDTO qcRequestDTO) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              qcRequestDTO, HttpMethod.POST, QC_MODEL_GET_FLAG, null, qcBaseUrl);
    } catch (IOException e) {
      log.error("Error while calling QC API for cn id {}", qcRequestDTO.getConsignmentId(), e);
      throw new ZoomException(
          "Error while calling QC API for cn id : " + qcRequestDTO.getConsignmentId());
    }
    TypeReference<QcResponseDTO> mapType = new TypeReference<QcResponseDTO>() {};

    return (QcResponseDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }
}
