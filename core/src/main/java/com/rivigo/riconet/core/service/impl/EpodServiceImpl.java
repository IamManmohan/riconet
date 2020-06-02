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

/**
 * This service is responsible for EPOD related tasks.
 *
 * @author Nikhil Rawat on 26/05/20.
 */
@Slf4j
@Service
public class EpodServiceImpl implements EpodService {

  /**
   * Object mapper for coverting the string payload to a DTO.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Autowired private ObjectMapper objectMapper;
  /**
   * consignment service for getting cn details from zoom backend.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Autowired private ConsignmentService consignmentService;

  /**
   * zoomBackendAPI client service for hitting zoom-backend api.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * function that coverts the dto String fetched from compass to EpodPreaparedDto, and hits zoom
   * backend to upload pod.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
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

  /**
   * function that coverts the dto String fetched from compass to EpodPreaparedDto.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  private EpodPreparedDto getEpodPreapredDTO(String json) {
    try {
      EpodPreparedDto epodPreparedDTO;
      epodPreparedDTO = objectMapper.readValue(json, EpodPreparedDto.class);
      return epodPreparedDTO;
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", json, ex);
      return null;
    }
  }
}
