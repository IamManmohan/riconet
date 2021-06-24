package com.rivigo.riconet.core.enums;

public enum ZoomCommunicationFieldNames {
  ID,
  CNOTE,
  CURRENT_LOCATION_ID,
  SECONDARY_CNOTES,
  OLD_CNOTE,
  CLIENT_ID,
  LOCATION_ID,
  TO_LOCATION_ID,
  CONSIGNMENT_ID,
  OU_CODE,
  STATUS,
  INSTRUMENT_NUMBER,
  DRAWEE_BANK,
  AMOUNT,
  DEPOSIT_DATE,
  PAYMENT_MODE,
  ORIGIN_FIELD_USER_NAME,
  ORIGIN_FIELD_USER_PHONE,
  DESTINATION_FIELD_USER_NAME,
  DESTINATION_FIELD_USER_PHONE,
  CREATED_BY,
  CONSIGNER_ADDRESS,
  CONSIGNEE_ADDRESS,
  ORGANIZATION_ID,
  PICK_UP_ID,
  FROM_LOCATION_ID,
  LAST_UPDATED_BY_ID,
  TICKET_ENTITY_ID,
  ACTION_NAME,
  ACTION_VALUE,
  FIRST_RIVIGO_OU,

  STALE_CATEGORY,
  TYPE_ID,

  PICKUP_CREATED_BY_USER_ID,
  PICKUP_CAPTAIN_NAME,
  PICKUP_CAPTAIN_CONTACT_NUMBER,

  CNOTE_TYPE,
  CONSIGNOR_USER_ID,
  CONSIGNEE_USER_ID,

  NAME, // tpm partner name
  MOBILE_NO, // tpm partner mobile number

  ONLINE_PAYMENT_LINK,
  FORWARD_CONSIGNMENT_ID,

  CONSIGNMENT_ALERT_ID,

  // flag to check if qc has been performed
  QC_DONE;

  public enum Consignment {
    DELIVERY_DATE_TIME
  }

  public enum Ticketing {
    ASSIGNEE_LOCATION_CODE,
    ASSIGNEE_EMAIL_ID,
    TICKET_ENTITY_ID
  }

  public enum CpbSummary {
    PROMISED_DELIVERY_DATE_TIME,
    SCHEDULED_TIME,
    IS_ASSUMED_DST_PC
  }

  public enum Reason {
    REASON,
    SUB_REASON
  }

  public enum Pickup {
    PICKUP_ID
  }

  public enum Wms {
    TASK_ID,
    PALLET_ID,
    PARENT_TASK_ID,
    TASK_TYPE,
    SHOP_FLOOR_ENABLED,
    USER_EMAIL_LIST,

    PARENT_ENTITY_ID,
    PARENT_ENTITY_TYPE,
  }

  public enum PaymentDetails {
    PAYMENT_MODE,
    PAYMENT_TYPE,
    TOTAL_AMOUNT,
    BEFORE_TOTAL_AMOUNT,
    TOTAL_AMOUNT_DIFF, // TOTAL_AMOUNT - BEFORE_TOTAL_AMOUNT
    BANK_NAME,
    TRANSACTION_REFERENCE_NO,
    TRANSFERRED_AMOUNT
  }

  // Field names for CN_UNDELIVERY event.
  public enum Undelivery {
    UNDELIVERED_AT,
    DELIVERY_REATTEMPT_CHARGEABLE
  }

  /** HolidayV2 field names used in HolidayV2 create and update event. */
  public enum HolidayV2 {
    /** location type of the holiday. */
    LOCATION_TYPE,
    /** location name of the holiday. */
    LOCATION_NAME,
    /** holiday start date time. */
    HOLIDAY_START_DATE_TIME,
    /** holiday end date time. */
    HOLIDAY_END_DATE_TIME,
    /** old holiday start date time. */
    OLD_HOLIDAY_START_DATE_TIME
  }

  public enum ConsignmentSchedule {
    DEPARTURE_TIME;
  }
}
