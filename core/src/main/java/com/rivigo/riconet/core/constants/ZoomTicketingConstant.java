package com.rivigo.riconet.core.constants;

public class ZoomTicketingConstant {

  private ZoomTicketingConstant() {
    throw new IllegalStateException("Utility class");
  }

  // id  of the ticket_type in zoom-ticketing database
  public static final Long QC_RECHECK_TYPE_ID = 1L;

  public static final Long QC_MEASUREMENT_TYPE_ID = 2L;

  public static final Long RETAIL_CHEQUE_BOUNCE_TYPE_ID = 70L;

  public static final String QC_GROUP_NAME = "QC";

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

  public static final String PRIORITY_AUTO_CLOSURE_MESSAGE="The Ticket got auto closed after changing the consignment priority";

  public static final String ZOOM_PROPERTIES_PRIORITY_SEPORATOR=",";
}
