package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.EpodPreparedDTO;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EpodService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EpodServiceImpl implements EpodService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public JsonNode uploadEpod(String json) {
    EpodPreparedDTO epodPreparedDTO = getEpodPreapredDTO(json);
    if (epodPreparedDTO != null) {
      ConsignmentUploadedFilesDTO consignmentUploadedFilesDTO = new ConsignmentUploadedFilesDTO();
      consignmentUploadedFilesDTO.setS3URL(epodPreparedDTO.getUrl());
      consignmentUploadedFilesDTO.setConsignmentId(
          consignmentService.getIdByCnote(epodPreparedDTO.getIdentifier()));
      consignmentUploadedFilesDTO.setFileName(epodPreparedDTO.getIdentifier());
      consignmentUploadedFilesDTO.setFileTypes("EPOD");
      return zoomBackendAPIClientService.uploadEpod(consignmentUploadedFilesDTO);
    } else {
      throw new ZoomException("EpodPreparedDTO cannot be null or empty.");
    }
  }

  private EpodPreparedDTO getEpodPreapredDTO(String json) {
    EpodPreparedDTO epodPreparedDTO;
    try {
      epodPreparedDTO = objectMapper.readValue(json, EpodPreparedDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", json, ex);
      return null;
    }
    return epodPreparedDTO;
  }
}
