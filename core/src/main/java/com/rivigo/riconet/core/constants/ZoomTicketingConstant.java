package com.rivigo.riconet.core.constants;

public class ZoomTicketingConstant {

  private ZoomTicketingConstant() {
    throw new IllegalStateException("Utility class");
  }

  // id  of the ticket_type in zoom-ticketing database
  public static final Long RETAIL_CHEQUE_BOUNCE_TYPE_ID = 70L;

  public static final Long WRITEOFF_TYPE_ID = 539L;

  public static final Long BANK_TRANSFER_TYPE_ID = 829L;

  public static final Long PICKUP_BANK_TRANSFER_TICKET_TYPE_ID = 830L;

  public static final Long UTR_BANK_TRANSFER_TICKET_TYPE_ID = 832L;

  public static final Long HQTR_LOCATION_ID = 9L;

  public static final String RTO_GROUP_TYPE_NAME = "RTO_Field";

  public static final String WRITE_OFF_ACTION_NAME = "write_off";

  public static final String PICKUP_BANK_TRANSFER_ACTION_NAME = "pickup_bank_transfer";

  public static final String UTR_BANK_TRANSFER_ACTION_NAME = "utr_bank_transfer";

  public static final String BANK_TRANSFER_ACTION_NAME = "cnote_bank_transfer";

  public static final String RETAIL_GROUP_NAME = "RETAIL";

  public static final String RESPONSE_KEY = "response";

  public static final String STATUS_KEY = "status";

  public static final String ERROR_MESSAGE_KEY = "errorMessage";

  public static final String ACTION_CLOSURE_MESSAGE =
      "Ticket got auto-closed after action is taken";

  public static final String PRIORITY_AUTO_CLOSURE_MESSAGE =
      "The Ticket got auto closed after changing the consignment priority";

  public static final String ZOOM_PROPERTIES_PRIORITY_SEPORATOR = ",";

  public static final String UNDERSCORE = "_";

  public static final String TICKET_ACTION_VALUE_APPROVE = "approve";

  public static final String BANK_TRANSFER_MESSAGE =
      "Please validate bank transfer request for %s: %s"
          + "<br>Bank Name: %s"
          + "<br>UTR No: %s"
          + "<br>Receipt URL: <a href=\"%s\">Click here</a>"
          + "<br>Amount to be paid: %s";

  public static final String BANK_TRANSFER_GROUP_NAME = "Finance Bank Transfer";

  public static final String BANK_TRANSFER_TICKET_TITLE = "Bank Transfer payment made for %s: %s";
  public static final String CNOTE_ADDED_TO_UTR = "Cnote: %s<br>Amount: %s";

  public static final Long RTO_TICKET_TYPE_ID = 731L;
}
