package com.rivigo.riconet.core.enums;

public enum ZoomPropertyName {
  PRIME_URL,
  SYSTEM_UNDER_MAINTENANCE,
  SYSTEM_UNDER_MAINTENANCE_MSG,

  DURATION_FOR_SCHEDULED_TRIP_CREATION_HRS,

  // DEPS
  MAX_DEPS_COUNT,
  /** ****** ********* ******** */

  // trip sync
  TRIP_SYNC_DELETE_ENABLED,
  TRIP_SYNC_INTERVAL_FOR_MATCH_IN_MINUTES,
  TRIP_SYNC_CUTOFF_DATE,
  TRIP_SYNC_TRIP_MATCH_MAX_DIFF_IN_PLACEMENT_TIME_MINUTES,
  /** ****** ********* ******** */

  // GPS update
  GPS_UPDATE_PLACEMENT_CUTOFF_DIFF_HOURS,
  GPS_TRIP_POSITION_UPDATE_TRIP_CREATION_FILTER_BY_DAYS,
  /** ****** ********* ******** */

  /** ****** GPS UPDATE HUB IN HUB OUT ******** */
  GPS_UPDATE_PAST_TRIP_GPS_IN_OUT,
  GPS_UPDATE_START_TIME_FOR_PAST_TRIP_GPS_IN_OUT,
  GPS_UPDATE_END_TIME_FOR_PAST_TRIP_GPS_IN_OUT,
  GPS_UPDATE_DEREGISTER_LINEHAUL_BEFORE_HOURS,
  GPS_UPDATE_FIRST_LEG_OFFSET_IN_HOURS,
  GPS_UPDATE_LAST_LEG_OFFSET_IN_HOURS,
  GPS_UPDATE_FIRST_LEG_PLACEMENT_OFFSET_IN_HOURS,
  /** ****** ********* ******** */

  /** ****** CONSIGNMENT ALERT DISPATCH DELAYED OFFSET ******** */
  CONSIGNMENT_ALERT_DISPATCH_DELAYED_BO_OFFSET_IN_MINUTES_P1,
  CONSIGNMENT_ALERT_DISPATCH_DELAYED_PC_OFFSET_IN_MINUTES_P1,
  CONSIGNMENT_ALERT_DISPATCH_DELAYED_BO_OFFSET_IN_MINUTES_P1_PLUS,
  CONSIGNMENT_ALERT_DISPATCH_DELAYED_PC_OFFSET_IN_MINUTES_P1_PLUS,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_ALERT_TIME,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_ALERT_LOCALTIME,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_EDD_NEXTDAY_ALERT_TIME,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_EDD_YESTER_OFFSET,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_EDD_SAMEDAY_OFFSET,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_UNDELIVERED_OFFSET,
  CONSIGNMENT_ALERT_PAST_DAYS_BUFFER,
  CONSIGNMENT_ALERT_ON_DEMAND_CLEANUP,
  CONSIGNMENT_ALERT_POD_PENDING_AFTER_DELIVERY_OFFSET_IN_MINUTES,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_COOLOFF_PC_IN_MINUTES,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_COOLOFF_BO_IN_MINUTES,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_CUTOFF_ARRIVAL_TIME_PC,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_COOLOFF_ARRIVAL_TIME_BO,
  CONSIGNMENT_ALERT_DELIVERY_PENDING_ALERT_GENERATION_START_TIME,
  CONSIGNMENT_ALERT_V2_DISPATCH_DELAYED_BO_OFFSET_IN_MINUTES,
  CONSIGNMENT_ALERT_V2_DISPATCH_DELAYED_PC_OFFSET_IN_MINUTES,
  CONSIGNMENT_ALERT_V2_DELIVERY_PENDING_COOLOFF_BO_IN_MINUTES,
  CONSIGNMENT_ALERT_V2_DELIVERY_PENDING_COOLOFF_PC_IN_MINUTES,
  CONSIGNMENT_ALERT_V2_DELIVERY_PENDING_CUTOFF_ARRIVAL_TIME_PC,
  CONSIGNMENT_ALERT_V2_DELIVERY_PENDING_ALERT_GENERATION_START_TIME,
  CONSIGNMENT_ALERT_V2_DELIVERY_PENDING_CUTOFF_ARRIVAL_TIME_BO,
  VEHICLE_DELAYED_ALERT_AT_SOURCE_OFFSET_BEFORE_SCHEDULED_TIME_IN_MINUES,
  VEHICLE_DELAYED_ALERT_AT_TOUCHING_OFFSET_BEFORE_SCHEDULED_TIME_IN_MINUES,
  CASHBOOK_CONSIGNMENT_INIT_DATE_TIME,
  /** ****** ********* ********* ********* ******** */

  /** ****** TRIP PLANNING ******** */
  INCLUDE_NOT_RECOMMENDED_CONSIGNMENTS_FOR_NATIONAL,
  INCLUDE_NOT_RECOMMENDED_CONSIGNMENTS_FOR_REGIONAL,
  INCLUDE_NOT_RECOMMENDED_CONSIGNMENTS_FOR_CLUSTER,
  INCLUDE_NOT_RECOMMENDED_CONSIGNMENTS_FOR_SHUTTLE,
  IS_AUTO_PLAN_ENABLED,
  WEIGHT_UTILISATION_LIMIT_PERCENT, // Session Property
  VOLUME_UTILISATION_LIMIT_PERCENT, // Session Property
  INCLUDE_INCOMING_CONSIGNMENTS_IN_PLANNING,
  INCOMING_CN_OFFSET_FOR_AUTO_PLAN_BEFORE_TRIP_DEPARTURE_IN_MINUTES,
  AUTO_PLAN_QC_PENDING_CNS,
  AUTO_PLAN_GREEN_CNS,
  PLANNING_ACTIVATION_CUTOFF_MINUTES,
  AUTO_PLAN_DISABLE_CUT_OFF_MINUTES,
  CONSIGNMENT_RECOVERY_BUFFER_MINUTES,
  CONSIGNMENT_DELAY_BUFFER_MINUTES,
  /** ****** ********* ********* ********* ******** */
  /** ************DHO PLANNING************** */
  DHO_INCLUDE_NOT_RECOMMENDED_CONSIGNMENTS,
  DHO_INCLUDE_INCOMING_CONSIGNMENTS_IN_PLANNING,
  DHO_IS_AUTO_PLAN_ENABLED,
  DHO_AUTO_PLAN_DISABLE_CUT_OFF_MINUTES,
  DHO_INCOMING_CN_OFFSET_FOR_AUTO_PLAN_BEFORE_CLOSING_IN_MINUTES,
  DHO_AUTO_PLAN_QC_PENDING_CNS,
  DHO_AUTO_PLAN_GREEN_CNS,

  /** ************************** */

  /** **************DELAY UPDATE SCHEDULER, DATA CLEANING SCHEDULER************ */
  REMOVE_TRIP_IF_UNUSED_FOR_X_MINUTES_AFTER_SCHEDULED_DISPATCH_TIME,
  UPDATE_TRIP_SCHEDULES_IF_DELAYED_MORE_THAN_X_MINUTES,
  CONSIDER_TRIPS_WITH_ORIGINAL_SCHEDULED_OUT_IN_LAST_N_DAYS,
  DELAY_TRIP_X_MINUTES_IN_FUTURE,
  CONSIDER_CONSIGNMENT_IF_DELAYED_MORE_THAN_X_MINUTES,
  CONSIDER_CONSIGNMENTS_CREATED_IN_LAST_N_DAYS,

  /** ********************* */
  ADMIN_OTP,
  CARGONET_ENABLED,
  CNOTERCA_WRITE_TO_FILE,

  SCHEDULE_MISS_PATROL_ENABLED,

  GOOGLE_TIME_MULTIPLIER,
  BACKFILL_EMAIL_ENABLED,
  TRIP_DISPATCH_BUFFER_FROM_NOW_IN_MINUTES,
  DRS_DISPATCH_BUFFER_FROM_NOW_IN_MINUTES,
  TRIP_ARRIVAL_BUFFER_IN_MINUTES,
  REMOVE_TRIP_IF_CREATED_BEFORE_DAYS,
  MAX_VALIDITY_DAYS_OF_ADHOC_SUGGESTION,
  DELETE_STALE_PRQ_BEFORE_N_DAYS,

  /** CONTINUUM STARTS */
  CONTINUUM_INIT_DATETIME,
  CONTINUUM_SCHEDULED_REPORT_DAYS,
  /** CONTINUUM ENDS */
  NO_LOGIN_ERROR_MESSAGE,
  SINGLE_LOGIN_ERROR_MESSAGE,
  UI_SCAN_ALLOWED_LOCATION_CODES,

  // stock check
  CLOSE_STOCK_CHECK_TASK_OFFSET_HOURS,
  BF_CPD_CALCULATION_BUFFER_IN_MINUTES,
  ROUTE_BLOCKED_ERROR_MSG,

  // Deps
  BLOCK_DEPS_IN_TASK_MSG,
  DEPS_EMAIL_TESTING,
  DEPS_EMAIL_ENABLED,
  SHORTAGE_NOTIFICATION_SUBJECT,
  SHORTAGE_NOTIFICATION_TEMPLATE,

  DOCUMENT_ISSUE_EMAIL_TESTING,
  DOCUMENT_ISSUE_EMAIL_ENABLED,
  DOCUMENT_ISSUE_NOTIFICATION_SUBJECT,
  DOCUMENT_ISSUE_NOTIFICATION_TEMPLATE,
  DOCUMENT_ISSUE_BUFFER_MINUTES

  // Session Properties
  ,
  SCAN_APP_LATEST_VERSION,
  SCAN_APP_MINIMUM_VERSION,
  SCAN_APP_UPDATE_MESSAGE,
  SCAN_APP_FORCED_UPDATE_MESSAGE,
  CAPTAIN_APP_LATEST_VERSION,
  CAPTAIN_APP_MINIMUM_VERSION,
  CAPTAIN_APP_UPDATE_MESSAGE,
  CAPTAIN_APP_FORCED_UPDATE_MESSAGE,
  MAX_WEIGHT_PER_BOX,
  MAX_ALLOWED_SYSTEM_GENERATED_BARCODES,
  MIN_DRS_DISPATCH_MILLIS_OF_DAY_FOR_ETA_CALCULATION,
  TESTING,
  MAX_DRS_DISPATCH_MILLIS_OF_DAY_FOR_ETA_CALCULATION,
  BOOLEAN_TESTING,
  PICKUP_REACHED_SMS_STRING,
  PICKUP_DELAYED_SMS_STRING,
  PICKUP_ASSIGNED_TO_USER_SMS_STRING,
  PICKUP_ASSIGNED_BP_USER_SMS_STRING,
  PICKUP_CREATED_SMS_STRING,
  PICKUP_DELAY_NOTIFICATION_SECONDS,
  DEFAULT_SMS_NUMBER,
  PICKUP_NOTIFICATION_ALLOWED_LOCATIONS,
  INTEGER_TESTING,
  APPOINTMENT_NOTIFICATION_ENABLED,
  APPOINTMENT_NOTIFICATION_TESTING,
  APPOINTMENT_DELIVERED_LATE_SAME_DAY_SUBJECT,
  APPOINTMENT_DELIVERED_LATE_SAME_DAY_EMAIL,
  APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_SUBJECT,
  APPOINTMENT_MISSED_SUBJECT,
  APPOINTMENT_MISSED_EMAIL,
  APPOINTMENT_MISSED_SUMMARY_SUBJECT,
  APPOINTMENT_MISSED_SUMMARY_EMAIL,
  APPOINTMENT_WRONG_UNDELIVERED_MARKED_SUBJECT,
  APPOINTMENT_WRONG_UNDELIVERED_MARKED_EMAIL,
  APPOINTMENT_NOT_OFD_EMAIL,
  APPOINTMENT_NOT_OFD_SUBJECT,
  RETAIL_COD_CN_CREATION_CONSIGNEE_SMS_STRING,
  RETAIL_COD_CN_CREATION_CONSIGNOR_SMS_STRING,
  RETAIL_PREPAID_CN_CREATION_CONSIGNOR_SMS_STRING,
  RETAIL_PREPAID_CN_CREATION_CONSIGNEE_SMS_STRING,
  RETAIL_PREPAID_CN_UPDATE_CONSIGNEE_SMS_STRING,
  RETAIL_PREPAID_CN_UPDATE_CONSIGNOR_SMS_STRING,
  RETAIL_COD_CN_UPDATE_CONSIGNEE_SMS_STRING,
  RETAIL_COD_CN_UPDATE_CONSIGNOR_SMS_STRING,
  RETAIL_COD_DRS_DISPATCH_CONSIGNEE_SMS_STRING,
  RETAIL_COLLECTION_CREATION_USER_SMS_STRING,
  RETAIL_COLLECTION_CREATION_BP_SMS_STRING,
  RETAIL_HANDOVER_USER_SMS_STRING,
  RETAIL_HANDOVER_BP_SMS_STRING,
  RETAIL_COLLECTION_CREATION_BP_CAPTAIN_SMS_STRING,
  APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_EMAIL,
  ZOOM_COMMUNICATION_DND_START_TIME,
  ZOOM_COMMUNICATION_DND_END_TIME,
  BF_PICKUP_CHARGE_PER_KG

  /** Rule engine consttants */
  ,
  MINIMUM_NUMBER_OF_CN_REQUIRED,
  MINIMUM_PICKUP_CHARGES_FOR_BF,
  REQUIRED_CLIENT_TYPE
}
