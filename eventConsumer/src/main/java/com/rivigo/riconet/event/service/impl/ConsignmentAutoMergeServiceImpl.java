package com.rivigo.riconet.event.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.SECONDARY_CNOTE_SEPARATOR;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.utils.ConsignmentUtils;
import com.rivigo.riconet.event.service.ConsignmentAutoMergeService;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.Consignment;
import java.io.IOException;
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

  private final ConsignmentService consignmentService;

  private final ApiClientService apiClientService;

  @Value("${zoom.url}")
  private String zoomBackendBaseUrl;

  @Override
  public void autoMergeSecondaryConsignment(NotificationDTO notificationDTO) {
    if (notificationDTO == null) {
      return;
    }
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    String location =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CURRENT_LOCATION_ID.name());
    if (!cnote.contains(SECONDARY_CNOTE_SEPARATOR)) {
      log.info("Cnote is not a secondary cn : {}", cnote);
      return;
    }
    log.info("Trying to merge child cnote : {}", cnote);
    String parentCnote = ConsignmentUtils.getPrimaryCnote(cnote);
    Consignment parentConsignment = consignmentService.getConsignmentByCnote(parentCnote);
    if (parentConsignment == null || parentConsignment.getLocationId() == null) {
      log.warn("Parent cnote not found : {}", parentCnote);
      return;
    }
    if (parentConsignment.getLocationId().toString().equals(location)) {
      try {
        List<String> mergeCnotes =
            consignmentService.getChildCnotesAtLocation(
                parentCnote, parentConsignment.getLocationId(), ConsignmentStatus.RECEIVED_AT_OU);
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
}
