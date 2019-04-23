package com.rivigo.riconet.core.constants;

/** Created by ashfakh on 24/09/18. */
public class PushNotificationConstant {

  private PushNotificationConstant() {
    throw new IllegalStateException("Utility class");
  }

  public static final String PRIORITY = "priority";
  public static final String TO = "to";
  public static final String DATA = "data";
  public static final String HIGH = "high";
  public static final String TASK_ID = "taskId";
  public static final String ENTITY_ID = "entityId";
  public static final String TIME_STAMP = "timeStamp";
  public static final String NOTIFICATION_TYPE = "notificationType";
  public static final String PARENT_TASK_ID = "parentTaskId";
  public static final String IB_CLEAR_EVENT = "IB_CLEAR";

  public static final String PICKUP_ASSIGNED_TO_USER_ID = "pickUpAssignedToUserId";
  public static final String PICKUP_ID = "pickUpId";
  public static final String PICKUP_CAPTAIN_NUMBER = "pickUpCaptainNumber";
  public static final String PICKUP_CAPTAIN_NAME = "pickUpCaptainName";

  public static final String CNOTE = "cnote";
  public static final String IS_TOPAY = "isToPay";
  public static final String PARTNER_MOBILE_NUMBER = "partnerMobileNumber";
  public static final String PARTNER_NAME = "partnerName";
  public static final String ONLINE_PAYMENT_LINK = "onlinePaymentLink";
  public static final String IS_CN_DELIVERY_DELAYED = "isDeliveryDelayed";

  // notification identifier required for express app
  public static final String NOTIFICATION_IDENTIFIER_KEY = "identifier";
  public static final String NOTIFICATION_BODY_AND_TITLE_KEY = "notification";
  public static final String NOTIFICATION_BODY_KEY = "body";
  public static final String NOTIFICATION_TITLE_KEY = "title";
  // identifiers
  public static final String PICKUP_ASSIGNED_NOTIFICATION_IDENTIFIER_VALUE = "PICKUP_ASSIGNED";
  public static final String PICKUP_REACHED_AT_CLIENT_WAREHOUSE_IDENTIFIER_VALUE =
      "PICKUP_REACHED_AT_CLIENT_WAREHOUSE";
  public static final String CN_LOADED_IDENTIFIER_VALUE = "DISPATCHED";
  public static final String TO_PAY_CN_OUT_FOR_DELIVERY_IDENTIFIER_VALUE =
      "OUT_FOR_DELIVERY_TO_PAY";
  public static final String PAID_CN_OUT_FOR_DELIVERY_IDENTIFIER_VALUE = "OUT_FOR_DELIVERY_PAID";

  public static final String PICKUP_ASSIGNED_NOTIFICATION_TITLE_VALUE = "Partner assigned";
  public static final String PICKUP_REACHED_AT_LOCATION_NOTIFICATION_TITLE_VALUE =
      "Partner reached";

  public static final String PICKUP_CANCELLATION_IDENTIFIER_VALUE = "PICKUP_CANCELLATION";
  public static final String PICKUP_CANCELLATION_NOTIFICATION_TITLE_VALUE = "Request cancelled";
  public static final String PICKUP_CANCELLATION_NOTIFICATION_BODY_VALUE =
      "We are sorry we could not cater to your pickup request. We hope to serve you again soon.";

  public static final String CN_LOADED_NOTIFICATION_TITLE_VALUE = "Dispatched";
  public static final String CN_DRS_DISPATCH_NOTIFICATION_TITLE_VALUE = "Out for delivery! ";
  public static final String CN_DELIVERED_NOTIFICATION_TITLE_VALUE = "Delivered";
}
