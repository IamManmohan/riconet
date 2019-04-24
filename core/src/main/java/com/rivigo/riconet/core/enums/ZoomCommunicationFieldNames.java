package com.rivigo.riconet.core.enums;

public enum ZoomCommunicationFieldNames {
  CNOTE,
  OLD_CNOTE,
  CLIENT_ID,
  LOCATION_ID,
  TO_LOCATION_ID,
  CONSIGNMENT_ID,
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
  USER_ID,
  TASK_ID,
  FROM_LOCATION_ID,
  LAST_UPDATED_BY_ID,
  TICKET_ENTITY_ID,
  PARENT_TASK_ID,
  TASK_TYPE,
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

  ONLINE_PAYMENT_LINK;

  public enum Consignment {
    DELIVERY_DATE_TIME
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

  public enum ConsignmentSchedule {
    DEPARTURE_CUTOFF_TIME,
    ARRIVAL_CUTOFF_TIME,
    ARRIVAL_TIME,
    DEPARTURE_TIME
  }

  public enum Pickup {
    PICKUP_ID
  }
}
