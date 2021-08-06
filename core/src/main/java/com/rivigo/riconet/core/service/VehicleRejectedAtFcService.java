package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;

/**
 * Service responsible for handling all actions related to {@link EventName#VEHICLE_REJECTED_AT_FC}.
 *
 * @author Nikhil Aggarwal
 * @since 6th August 2021
 */
public interface VehicleRejectedAtFcService {

  /**
   * Method used to process received {@link EventName#VEHICLE_REJECTED_AT_FC} to mark all attached
   * consignments as undelivered.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  void processVehicleRejectionEventToUndeliverCns(NotificationDTO notificationDTO);
}
