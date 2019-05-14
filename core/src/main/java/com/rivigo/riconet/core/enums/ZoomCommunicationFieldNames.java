package com.rivigo.riconet.core.enums;

public enum ZoomCommunicationFieldNames {
  CNOTE,
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

  public enum Pickup {
    PICKUP_ID
  }

  public enum Wms {
    TASK_ID,
    PALLET_ID,
    PARENT_TASK_ID,
    TASK_TYPE,
    SHOP_FLOOR_ENABLED,
    USER_EMAIL_LIST
  }
}
