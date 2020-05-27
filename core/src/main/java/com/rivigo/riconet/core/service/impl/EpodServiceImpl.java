package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.EpodConstants;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.EpodPreparedDto;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EpodService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EpodServiceImpl implements EpodService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public JsonNode uploadEpod(String json) {
    final EpodPreparedDto epodPreparedDTO = getEpodPreapredDTO(json);
    if (epodPreparedDTO == null) {
      throw new ZoomException("EpodPreparedDTO cannot be null or empty.");
    } else {
      final ConsignmentUploadedFilesDTO consignmentUploadedFilesDTO =
          new ConsignmentUploadedFilesDTO();
      consignmentUploadedFilesDTO.setS3URL(epodPreparedDTO.getUrl());
      consignmentUploadedFilesDTO.setConsignmentId(
          consignmentService.getIdByCnote(epodPreparedDTO.getIdentifier()));
      consignmentUploadedFilesDTO.setFileName(epodPreparedDTO.getIdentifier());
      consignmentUploadedFilesDTO.setFileTypes(EpodConstants.EPOD);
      return zoomBackendAPIClientService.uploadEpod(consignmentUploadedFilesDTO);
    }
  }

  private EpodPreparedDto getEpodPreapredDTO(String json) {
    EpodPreparedDto epodPreparedDTO;
    try {
      epodPreparedDTO = objectMapper.readValue(json, EpodPreparedDto.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", json, ex);
      return null;
    }
    return epodPreparedDTO;
  }
}
