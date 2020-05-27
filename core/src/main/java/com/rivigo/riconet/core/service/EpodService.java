package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/** This is the common service for all epod tasks. */
@Service
public interface EpodService {

  /**
   * This function is used to update epod link.
   *
   * @param json string which is converted to consignmentUploadedFilesDTO for further api calls.
   * @return upload the s3 url for the epod in consignment_uploaded_files.
   */
  JsonNode uploadEpod(String json);
}
