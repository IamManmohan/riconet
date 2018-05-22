package com.rivigo.riconet.core.enums;

/**
 * Created by aditya on 19/2/18.
 */
public enum EventName {
  DEFAULT,
  CN_COMPLETION_ALL_INSTANCES,
  CN_RECEIVED_AT_OU,
  CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU,
  CN_RECIEVED_AT_OU_ALL_INSTANCES,
  CN_CNOTE_TYPE_CHANGED_FROM_NORMAL,

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
  CN_DELIVERY_LOADED,

  CLIENT_USER_CREATION,

  COLLECTION_CHEQUE_BOUNCE,
}
