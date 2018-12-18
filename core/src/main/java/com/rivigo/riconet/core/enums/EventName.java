package com.rivigo.riconet.core.enums;

/** Created by aditya on 19/2/18. */
public enum EventName {
  DEFAULT,
  CN_COMPLETION_ALL_INSTANCES,
  CN_RECEIVED_AT_OU,
  CN_LOADED,
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
  CN_OUT_FOR_DELIVERY,
  CN_UNDELIVERY,
  CN_APPOINTMENT_CONFIRMED,
  CN_PAYMENT_DETAILS_V2_UPDATE,
  CN_DELIVERY,
  CN_DELIVERY_LOADED,
  CN_DELETED,
  CN_TRIP_DISPATCHED,
  CN_PAYMENT_HANDOVER_COMPLETED,
  CLIENT_USER_CREATION,
  CN_STALE,

  COLLECTION_CHEQUE_BOUNCE,
  CN_COLLECTION_CHEQUE_BOUNCE_TICKET_CLOSED,

  UNLOADING_IN_LOADING,
  CN_TOTAL_BOXES_CHANGE,
  CN_LOADING_PLAN_UNPLAN,
  CN_UNLOADING_PLAN_UNPLAN,

  // wms
  PALLET_CLOSED,
  TASK_CLOSED_OR_REASSIGNED,

  CN_CNOTE_CHANGE,
  CN_DEPS_CREATION,
  CN_DEPS_CREATION_FROM_CONSIGNMENT_HISTORY,

  /** Finance Events* */
  CLIENT_CREATE_UPDATE,

  /** TicketingFieldName Event * */
  TICKET_CREATION,
  TICKET_ASSIGNEE_CHANGE,
  TICKET_STATUS_CHANGE,
  TICKET_ESCALATION_CHANGE,
  TICKET_CC_NEW_PERSON_ADDITION,
  TICKET_ACTION,
  TICKET_SEVERITY_CHANGE,
  TICKET_COMMENT_CREATION,

  /** Online Payment* */
  ONLINE_TRANSACTION_PAYMENT_FAILURE,
  ONLINE_TRANSACTION_SEND_PAYMENT_LINK,

  /** CPB Summary Event * */
  CN_ASSUMED_DST_PC_SCHEDULE_NOT_REACHED,
  CN_ASSUMED_DST_PC_SCHEDULE_REACHED,
  CN_ASSUMED_SRC_PC_SCHEDULE_CREATE,
  CN_ASSUMED_SRC_PC_SCHEDULE_UPDATE,
  CN_DELIVERY_PENDING_ALERT,
  CN_DISPATCH_DELAYED_UNPLAN_ALERT,
  LINEHAUL_TRIP_ASSIGNMENT,
  LINEHAUL_TRIP_DISPATCH,
  LINEHAUL_TRIP_DELETED,
  CN_REACHED_AT_LOCATION,
  CN_LEFT_FROM_LOCATION,
  TRIP_TRACKING_DISPATCH_FROM_LOCATION,
  TRIP_TRACKING_REACHED_AT_LOCATION,
  CN_NOT_AUTO_PLANNED,
  CN_PROMISED_DELIVERY_DATE_TIME_CHANGE,
  CN_SCHEDULE_CACHE_DELETE_UNPLAN;
}
