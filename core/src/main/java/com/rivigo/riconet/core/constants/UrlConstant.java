package com.rivigo.riconet.core.constants;

public class UrlConstant {

  private UrlConstant() {
    throw new IllegalStateException("Utility class");
  }

  public static final String ZOOM_TICKETING_GET_BY_CNOTE_AND_TYPE = "/ticket/entityandtype";

  public static final String ZOOM_TICKETING_POST_PUT_TICKET = "/ticket";

  public static final String ZOOM_TICKETING_GET_GROUP_ID = "/group/locationandgroupname";

  public static final String ZOOM_TICKETING_POST_COMMENT = "/comment";

  public static final String ZOOM_BACKEND_UPDATE_QC_CHECK =
      "/operations/consignments/qc/updateQcCheck";

  public static final String ZOOM_BACKEND_HANDLE_QC_BLOCKER =
      "/operations/consignments/qc/handleQcBlocker";

  public static final String ZOOM_BACKEND_BF_CPD_CALCULATION =
      "/operations/consignments/bfCpdCalculation";

  public static final String ZOOMBOOK_TRANSACTION_V2 = "/zoombook/transaction/v2";

  public static final String ZOOM_BACKEND_POLICY_GENERATION = "/insurance/policy/";

  public static final String ZOOM_BACKEND_CLIENT_SERVICE = "/master/client";

  public static final String ZOOM_BACKEND_ORGANIZATION_SERVICE = "/operations/organization";

  public static final String ZOOM_BACKEND_CONSIGNMENT_INVOICE = "/master/consignmentInvoice";

  public static final String ZOOM_BACKEND_ASSET_ONBOARDING = "/asset/onBoard/{cnId}";

  public static final String ZOOM_BACKEND_CONSIGNMENT_BLOCKER = "/consignmentBlocker";

  public static final String ZOOM_BILLING_CN_DETAILS = "/external/chargedweight/cnote/{cnote}";

  public static final String ZOOM_TICKETING_GET_COMMENTS = "/comment";

  public static final String ZOOM_TICKETING_TICKET_DETAIL = "/ticket/detail";

  public static final String PRIORITY_URL = "/operations/consignments/priority";

  public static final String ZOOM_BACKEND_VAS_DETAILS_SERVICE = "/master/vasDetails";

  public static final String ZOOM_BACKEND_WRITE_OFF_REQUEST_ONBOARDING =
      "/operations/retail/collections/{cnote}/writeOff/{requestAction}";

  public static final String WMS_TASK_BY_TRIP_LOCATION_AND_TYPE = "/enriched/task";
}
