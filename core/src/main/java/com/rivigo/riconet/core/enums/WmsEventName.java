package com.rivigo.riconet.core.enums;

public enum WmsEventName {
  // Notify scan app user
  CN_INBOUND_CLEAR,
  MANIFEST_CLOSED,
  PALLET_CLOSED,
  SHOP_FLOOR_STATUS_UPDATE,
  TASK_CLOSED_OR_CANCELLED,
  TASK_SUBMITTED_ANOTHER_USER,
  TASK_UNASSIGNED,
  TASK_UPSERT,
  CN_LOADING_PLAN_UNPLAN,
  CN_TOTAL_BOXES_CHANGE,
  CN_REMOVED_FROM_UNLOADING,

  // catch task closure for RTO CN processing
  TASK_CLOSED,

  /** Event made for closing RTO pace tickets upon creation of RTO_REVERSE task */
  RTO_REVERSE_TASK_OPEN
}
