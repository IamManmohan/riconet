package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.DemurrageService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DemurrageService is responsible for demurrage related tasks. <br>
 * To summarise: Demurrage End time will be set as FINAL DELIVERY TIME, and Start time will be set
 * as FIRST DELIVERY FAILURE DUE TO CLIENT FACING REASONS.
 *
 * @author Nikhil Aggarwal
 * @date 10-Aug-2020
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DemurrageServiceImpl implements DemurrageService {

  /** ZoomBackendAPIClientService is used to make API calls to backend to start/end demurrage. */
  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when event CN_UNDELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processEventToStartDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    final String startTime =
        metadata.get(ZoomCommunicationFieldNames.Undelivery.UNDELIVERED_AT.name());
    log.debug(
        "Start demurrage request for cnote {} starting at time {} received.", cnote, startTime);
    final String undeliveredCnRecordId = metadata.get(ZoomCommunicationFieldNames.ID.name());
    zoomBackendAPIClientService.startDemurrage(cnote, startTime, undeliveredCnRecordId);
  }

  /**
   * Function used to end demurrage for given consignment. <br>
   * This function is called when event CN_DELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to end demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processEventToEndDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    final String deliveryDateTime =
        metadata.get(ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name());
    log.debug(
        "End demurrage request for cnote {} delivered at time {} received.",
        cnote,
        deliveryDateTime);
    zoomBackendAPIClientService.endDemurrage(cnote);
  }

  /**
   * Function used to cancel ongoing demurrage for given consignment. <br>
   * This function is called when event CN_DELETED, CN_STALE and DEPS_RECORD_CREATION is triggered.
   * <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to cancel ongoing demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processEventToCancelDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    log.debug("Cancel ongoing demurrage request for cnote {} received.", cnote);
    zoomBackendAPIClientService.cancelDemurrage(cnote);
  }
}
