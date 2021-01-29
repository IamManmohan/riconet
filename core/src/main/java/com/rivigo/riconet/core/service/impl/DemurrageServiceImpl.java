package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.DemurrageService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DemurrageService is responsible for all demurrage related events. <br>
 * It parses the notificationDTO received in the events and hits backend API to start, end or cancel
 * demurrage.
 *
 * @author Nikhil Aggarwal
 * @date 29-Jan-2021
 * @version 2
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DemurrageServiceImpl implements DemurrageService {

  /**
   * ZoomBackendAPIClientService is used to make API calls to backend to start/end/cancel demurrage.
   */
  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when event {@link EventName#CN_UNDELIVERY} is consumed. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processCnUndeliveryEventToStartDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    final String startTime =
        metadata.get(ZoomCommunicationFieldNames.Undelivery.UNDELIVERED_AT.name());
    log.debug(
        "Start demurrage request for cnote {} starting at time {} received.", cnote, startTime);
    final String undeliveredCnRecordId = metadata.get(ZoomCommunicationFieldNames.ID.name());
    zoomBackendAPIClientService.startDemurrageOnCnUndelivery(
        cnote, startTime, undeliveredCnRecordId);
  }

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when any of the following events are consumed: {@link
   * EventName#CN_DISPATCH_HOLD} or {@link EventName#CN_DELIVERY_HOLD}. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processCnDispatchDeliveryHoldEventToStartDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String consignmentId = metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name());
    final String consignmentAlertId =
        metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ALERT_ID.name());
    boolean isDispatch;
    if (EventName.CN_DELIVERY_HOLD.name().equals(notificationDTO.getEventName())) {
      isDispatch = false;
    } else if (EventName.CN_DISPATCH_HOLD.name().equals(notificationDTO.getEventName())) {
      isDispatch = true;
    } else {
      throw new ZoomException(
          "Invalid event name: {} for start demurrage request.", notificationDTO.getEventName());
    }
    log.debug(
        "Start demurrage request for consignment id: {} on dispatch/delivery hold received.",
        consignmentId);
    zoomBackendAPIClientService.startDemurrageOnCnDispatchOrDeliveryHold(
        consignmentId, consignmentAlertId, isDispatch);
  }

  /**
   * Function used to end demurrage for given consignment. <br>
   * This function is called when event {@link EventName#CN_DELIVERY} is consumed. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
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
   * This function is called when any of the following events are consumed: {@link
   * EventName#CN_DELETED}, {@link EventName#CN_STALE} or {@link EventName#DEPS_RECORD_CREATION}.
   * <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
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
