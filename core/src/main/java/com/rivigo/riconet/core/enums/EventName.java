package com.rivigo.riconet.core.enums;

/**
 * Created by aditya on 19/2/18.
 */
public enum EventName {
  DEFAULT,
  /**
   * Events to be consumed by external users
   */
  CN_CREATION,
  CN_COMPLETION,
  PICKUP_CREATION,
  PICKUP_CANCELLATION,
  PICKUP_COMPLETION,
  PICKUP_RESCHEDULE,
  PICKUP_EDITING,
  PICKUP_REACHED_AT_CLIENT_WAREHOUSE,
  PICKUP_FAILURE,
  PICKUP_ASSIGNMENT,
  PICKUP_REASSIGNMENT,
  CN_EDITING,
  CN_UNLOAD,
  CN_DRS_DISPATCH,
  CN_UNDELIVERY,
  CN_APPOINTMENT_CONFIRMED,
  CN_PAYMENT_DETAILS_V2_UPDATE,
  CN_DELIVERY,

  /**
   * Events to be consumed by system
   */
  CLIENT_USER_CREATION,
}
