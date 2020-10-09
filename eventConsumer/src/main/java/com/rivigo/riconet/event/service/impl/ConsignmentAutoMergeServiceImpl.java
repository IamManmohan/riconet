package com.rivigo.riconet.event.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.event.service.ConsignmentAutoMergeService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsignmentAutoMergeServiceImpl implements ConsignmentAutoMergeService {

  private final ApiClientService apiClientService;

  @Value("${zoom.url}")
  private String zoomBackendBaseUrl;

  @Override
  public void autoMergeConsignments(NotificationDTO notificationDTO) {
    if (notificationDTO == null) {
      return;
    }
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    log.info("Trying to merge cnote : {}", cnote);
    List<String> mergeCnotes =
        Arrays.asList(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.SECONDARY_CNOTES.name())
                .split(","));
    try {
      JsonNode responseJson =
          apiClientService.getEntity(
              mergeCnotes, HttpMethod.POST, "/deps/auto-merge", null, zoomBackendBaseUrl);
      log.debug("response {}", responseJson);
    } catch (IOException e) {
      log.error(
          "Exception occurred while merging cn in zoom backend {}",
          ExceptionUtils.getFullStackTrace(e));
    }
  }
}
