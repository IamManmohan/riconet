package com.rivigo.riconet.core.enums;

public enum EventName {
  CN_RECEIVED_AT_OU,
  CN_LOADED,

  PICKUP_CANCELLATION,
  PICKUP_COMPLETION,
  PICKUP_REACHED_AT_CLIENT_WAREHOUSE,
  PICKUP_ASSIGNMENT,

  CN_DRS_DISPATCH,
  CN_DELIVERY,
  CN_DELETED,
  CN_TRIP_DISPATCHED,
  CN_PAYMENT_HANDOVER_COMPLETED,
  CN_STALE,
  CN_UNDELIVERY,

  COLLECTION_CHEQUE_BOUNCE,
  BANK_TRANSFER_INITIATED,

  /** TicketingFieldName Event * */
  TICKET_CREATION,
  TICKET_ASSIGNEE_CHANGE,
  RTO_TICKET_ASSIGNEE_CHANGE,
  TICKET_STATUS_CHANGE,
  TICKET_ESCALATION_CHANGE,
  TICKET_CC_NEW_PERSON_ADDITION,
  TICKET_ACTION,
  TICKET_SEVERITY_CHANGE,
  TICKET_COMMENT_CREATION,

  /** Datastore address cleanup * */
  CONSIGNMENT_EWAYBILL_METADATA_CREATION_ADDRESS_CLEANUP,

  /** Error Correction Events */
  CONSIGNMENT_QC_DATA_UPSERT,

  /** Events yo trigger invoice generation. */
  CN_DELIVERY_CLUSTER_SCAN_IN,
  CN_DRS_PLANNED,

  /** DEPS record creation event. */
  DEPS_RECORD_CREATION,

  /** HolidayV2 creation event. */
  HOLIDAY_V2_CREATE,
  /** HolidayV2 update or deletion event. */
  HOLIDAY_V2_UPDATE,

  /** Consignment's trip dispatch hold event. */
  CN_DISPATCH_HOLD,
  /** Consignment's delivery hold event. */
  CN_DELIVERY_HOLD,

  /** Vehicle rejected at FC event. */
  VEHICLE_REJECTED_AT_FC,
}
