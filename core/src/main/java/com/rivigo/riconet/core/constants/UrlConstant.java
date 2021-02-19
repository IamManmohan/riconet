package com.rivigo.riconet.core.constants;

import lombok.experimental.UtilityClass;

/** This file contains the various external URLs. */
@UtilityClass
public class UrlConstant {
  /** Zoom Backend API to generate final invoice for rivigo to pay CN's. */
  public static final String ZOOM_BACKEND_GENERATE_INVOICE = "/operations/retail/invoice";

  public static final String ZOOM_TICKETING_GET_BY_CNOTE_AND_TYPE = "/ticket/entityandtype";

  public static final String ZOOM_TICKETING_GET_BY_ENTITY_IN_AND_TYPE = "/ticket/entityinandtype";

  public static final String ZOOM_TICKETING_POST_PUT_TICKET = "/ticket";

  public static final String ZOOM_TICKETING_GET_GROUP_ID = "/group/locationandgroupname";

  public static final String ZOOM_TICKETING_POST_COMMENT = "/comment";

  public static final String ZOOM_TICKETING_TICKET_ACTION = "/ticket-action";

  public static final String ZOOM_BACKEND_BF_CPD_CALCULATION =
      "/operations/consignments/bfCpdCalculation";

  public static final String ZOOMBOOK_TRANSACTION_V2 = "/zoombook/transaction/v2";

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

  public static final String ZOOM_BACKEND_KNOCK_OFF_REQUEST =
      "/operations/retail/collections/{cnote}/knockOff";

  public static final String ZOOM_DATASTORE_EWAYBILL_METADATA_CLEANUP = "/address/cleanup/ewaybill";

  public static final String WMS_SERVICE_TASK_CREATION = "/tasks";

  public static final String ZOOM_BACKEND_MARK_HANDOVER_AS_RECOVERY_PENDING =
      "/operations/retail/collections/handover/markRecoveryPending";

  public static final String ZOOM_BACKEND_MARK_HANDOVER_AS_RECOVERY_PENDING_BULK =
      "/operations/retail/collections/handover/markRecoveryPendingBulk";

  public static final String ZOOM_BACKEND_CANCEL_PICKUP = "/operations/pickup/cancel/V2";

  public static final String ZOOM_BACKEND_CREATE_BP = "/master/partner";

  public static final String ZOOM_BACKEND_CREATE_VENDOR = "/master/feeder_vendor";

  public static final String ZOOM_BACKEND_PROCESS_VEHICLE_EVENT =
      "/primeSync/processVehicleEvent/{tripId}";

  public static final String ZOOM_BACKEND_PROCESS_ATHENA_GPS_EVENT = "/athenaGpsEvent";

  public static final String ZOOM_BACKEND_QC_CONSIGNMENT_V2 = "/operations/consignments/qc/v2";

  public static final String ZOOM_BACKEND_MARK_DELIVERED =
      "/operations/consignments/markFnCnDelivered/{cnote}";

  /** End point for EPOD prepared, after this it would be uploaded in consignment_uploaded_files. */
  public static final String ZOOM_BACKEND_UPLOAD_EPOD = "/epod/uploadPod";

  /** End point for epod flag in clients. */
  public static final String ZOOM_BACKEND_UPDATE_EPOD_FLAG = "/epod/updateEpodFlag";

  /** Backend API endpoint to start demurrage for a consignment on CN undelivery. */
  public final String ZOOM_BACKEND_START_DEMURRAGE_UNDELIVERY = "/vas/demurrage/undelivery/start";

  /** Backend API endpoint to start demurrage for a consignment on CN dispatch hold. */
  public final String ZOOM_BACKEND_START_DEMURRAGE_DISPATCH_HOLD =
      "/vas/demurrage/dispatch-hold/start";

  /** Backend API endpoint to start demurrage for a consignment on CN delivery hold. */
  public final String ZOOM_BACKEND_START_DEMURRAGE_DELIVERY_HOLD =
      "/vas/demurrage/delivery-hold/start";

  /** Backend API endpoint to complete demurrage for a consignment. */
  public final String ZOOM_BACKEND_END_DEMURRAGE = "/vas/demurrage/end";

  /** Backend API endpoint to cancel ongoing demurrage for a consignment. */
  public final String ZOOM_BACKEND_CANCEL_DEMURRAGE = "/vas/demurrage/cancel";

  /** Backend API to add client level blockers based on client credit limit breach event. */
  public static final String BLOCK_UNBLOCK_CLIENT = "/master/client/addUpdateClientLimitBlocker";

  /** Backend API endpoint to trigger CPD calculation for all affected CNs due to holiday update. */
  public static final String ZOOM_BACKEND_TRIGGER_CPD_CALCULATIONS_HOLIDAY =
      "/holiday/cpd-calculation";

  /** Backend API to knockoff given UTR number for payment type Bank Transfer. */
  public final String ZOOM_BACKEND_KNOCKOFF_COMPLETE_UTR_BANK_TRANSFER =
      "/payment/bank-transfer/utr/knockOff/complete";

  /** Backend API to revert knockoff given UTR number for payment type Bank Transfer. */
  public final String ZOOM_BACKEND_REVERT_KNOCKOFF_UTR_BANK_TRANSFER =
      "/payment/bank-transfer/utr/knockOff/revert";
}
