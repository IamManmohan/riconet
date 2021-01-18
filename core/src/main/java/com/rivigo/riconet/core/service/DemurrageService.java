package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/**
 * DemurrageService is responsible for demurrage related tasks.
 *
 * @author Nikhil Aggarwal
 * @date 10-Aug-2020
 */
public interface DemurrageService {

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when event CN_UNDELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processEventToStartDemurrage(NotificationDTO notificationDTO);

  /**
   * Function used to end demurrage for given consignment. <br>
   * This function is called when event CN_DELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to end demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processEventToEndDemurrage(NotificationDTO notificationDTO);

  /**
   * Function used to cancel ongoing demurrage for given consignment. <br>
   * This function is called when event CN_DELETED, CN_STALE and DEPS_RECORD_CREATION is triggered.
   * <br>
   * This function is used to fetch approriate fields from input NotificationDTO and makes an API
   * call to backend service to cancel ongoing demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processEventToCancelDemurrage(NotificationDTO notificationDTO);
}
