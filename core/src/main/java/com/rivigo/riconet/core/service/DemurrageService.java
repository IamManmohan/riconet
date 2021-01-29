package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;

/**
 * DemurrageService is responsible for all demurrage related events. <br>
 * It parses the notificationDTO received in the events and hits backend API to start, end or cancel
 * demurrage.
 *
 * @author Nikhil Aggarwal
 * @date 29-Jan-2021
 * @version 2
 */
public interface DemurrageService {

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when event {@link EventName#CN_UNDELIVERY} is consumed. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processCnUndeliveryEventToStartDemurrage(NotificationDTO notificationDTO);

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when any of the following events are consumed: {@link
   * EventName#CN_DISPATCH_HOLD} or {@link EventName#CN_DELIVERY_HOLD}. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processCnDispatchDeliveryHoldEventToStartDemurrage(NotificationDTO notificationDTO);

  /**
   * Function used to end demurrage for given consignment. <br>
   * This function is called when event {@link EventName#CN_DELIVERY} is consumed. <br>
   * This function is used to fetch appropriate fields from input NotificationDTO and makes an API
   * call to backend service to end demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processEventToEndDemurrage(NotificationDTO notificationDTO);

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
  void processEventToCancelDemurrage(NotificationDTO notificationDTO);
}
