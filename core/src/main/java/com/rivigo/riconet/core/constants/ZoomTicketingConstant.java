package com.rivigo.riconet.core.constants;

public class ZoomTicketingConstant {

  private ZoomTicketingConstant() {
    throw new IllegalStateException("Utility class");
  }

  // id  of the ticket_type in zoom-ticketing database
  public static final Long QC_RECHECK_TYPE_ID = 1L;

  public static final Long QC_MEASUREMENT_TYPE_ID = 2L;

  public static final Long RETAIL_CHEQUE_BOUNCE_TYPE_ID = 70L;

  public static final Long QC_BLOCKER_TYPE_ID = 75L;

  public static final Long WRITEOFF_TYPE_ID = 539L;

  public static final Long BANK_TRANSFER_TYPE_ID = 829L;

  public static final Long PICKUP_BANK_TRANSFER_TICKET_TYPE_ID = 830L;

  public static final Long UTR_BANK_TRANSFER_TICKET_TYPE_ID = 832L;

  public static final Long HQTR_LOCATION_ID = 9L;

  public static final String QC_GROUP_NAME = "QC";

  public static final String RTO_GROUP_TYPE_NAME = "RTO_Field";

  public static final String QC_ACTION_NAME = "qc";

  public static final String WRITE_OFF_ACTION_NAME = "write_off";

  public static final String PICKUP_BANK_TRANSFER_ACTION_NAME = "pickup_bank_transfer";

  public static final String UTR_BANK_TRANSFER_ACTION_NAME = "utr_bank_transfer";

  public static final String BANK_TRANSFER_ACTION_NAME = "cnote_bank_transfer";

  public static final String RETAIL_GROUP_NAME = "RETAIL";

  public static final String QC_TASK_TITLE = "Qc task";

  public static final String QC_RECHECK_TASK_CREATION_MESSAGE =
      "Complete the QC verification task by clicking \"COMPLETE QC\" button at the top";

  public static final String QC_MEASUREMENT_TASK_CREATION_MESSAGE =
      "Complete the QC measurement task by clicking \"COMPLETE QC\" button at the top";

  public static final String RESPONSE_KEY = "response";

  public static final String STATUS_KEY = "status";

  public static final String ERROR_MESSAGE_KEY = "errorMessage";

  public static final String QC_AUTO_CLOSURE_MESSAGE_DISPATCH =
      "The ticket got auto-closed because the CN has been dispatched from the Source OU, without QC completion";

  public static final String QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP =
      "Ticket got auto-closed due to non availability of QC at the Source OU";

  public static final String QC_AUTO_CLOSURE_MESSAGE_CNOTE_TYPE_CHANGE =
      "The task got auto closed because of change of CN from credit to retail";

  public static final String QC_AUTO_CLOSURE_DEPS_CREATION =
      "The task got auto closed because consignment is marked as DEPS";

  public static final String ACTION_CLOSURE_MESSAGE =
      "Ticket got auto-closed after action is taken";

  public static final String QC_BLOCKER_AUTO_CLOSURE_MESSAGE =
      "Ticket Got Closed, as there was no Communication Email ID available for the client";

  public static final String PRIORITY_AUTO_CLOSURE_MESSAGE =
      "The Ticket got auto closed after changing the consignment priority";

  public static final String ZOOM_PROPERTIES_PRIORITY_SEPORATOR = ",";

  public static final String UNDERSCORE = "_";

  public static final String TICKET_ACTION_VALUE_APPROVE = "approve";

  public static final String TICKET_QC_BLOCKER_FAILURE_COMMENT =
      "Ticket action failed because of %s . Reopened now. Please take appropriate action and try again";

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
