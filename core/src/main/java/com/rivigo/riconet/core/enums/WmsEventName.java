package com.rivigo.riconet.core.enums;

public enum WmsEventName {
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

  @Deprecated
  TASK_CLOSED_OR_REASSIGNED,
  @Deprecated
  IB_CLEAR,
  @Deprecated
  CN_UNLOADING_PLAN_UNPLAN
}
