package com.rivigo.riconet.core.constants;

/** Created by Deepanshu on 19 May, 2019 */
public final class ExpressAppConstants {

  private ExpressAppConstants() {
    throw new IllegalStateException("Utility class");
  }

  public class NotificationKey {
    public static final String IDENTIFIER = "identifier";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_DATA = "notificationData";
    public static final String BODY = "body";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String ACTIONS = "actions";
  }

  public class NotificationIdentifier {
    public static final String CN_FIRST_OU_DISPATCH = "DISPATCHED";
    public static final String PICKUP_ASSIGNED = "PICKUP_ASSIGNED";
    public static final String PICKUP_REACHED_AT_CLIENT_WAREHOUSE =
        "PICKUP_REACHED_AT_CLIENT_WAREHOUSE";
    public static final String TO_PAY_CN_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY_TO_PAY";
    public static final String PAID_CN_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY_PAID";
    public static final String PICKUP_CANCELLATION = "PICKUP_CANCELLATION";
  }

  public class NotificationTitle {
    public static final String CN_FIRST_OU_DISPATCH = "Dispatched";
    public static final String PICKUP_ASSIGNED = "Partner assigned";
    public static final String PICKUP_REACHED_AT_CLIENT_WAREHOUSE = "Partner reached";
    public static final String CN_DRS_DISPATCHED = "Out for delivery! ";
    public static final String CN_DELIVERED = "Delivered";
    public static final String PICKUP_CANCELLATION = "Request cancelled";
  }

  public class NotificationBody {
    public static final String PICKUP_CANCELLATION =
        "We are sorry we could not cater to your pickup request. We hope to serve you again soon.";
    public static final String PICKUP_ASSIGNED = "Your pickup partner has been assigned!";
    public static final String PICKUP_REACHED_AT_CLIENT_WAREHOUSE =
        "Knock, knock! We have reached your location for pickup.";
    public static final String CN_FIRST_OU_DISPATCH = "Your shipment %s has been dispatched!";
    public static final String CN_DRS_DISPATCHED = "Your shipment %s is out for delivery! ";
  }

  public class PageUrl {
    public static final String APP_URL = "https://rivigo.com/express";
    public static final String LOGIN = "/login";
    public static final String TRACK_CN = "/track-consignment/%s";
    public static final String BOOKING = "/booking";
    public static final String POD = "/pod";
    public static final String TRACK_POD = "/track-pod/%s";
    public static final String CREATE_BOOKING = "/create-booking";
    public static final String CALL = "/call/%s";
  }

  public class NotificationAction {
    public static final String OPEN = "OPEN";
    public static final String TRACK = "TRACK";
    public static final String CALL = "CALL";
    public static final String POD = "VIEW POD";
  }
}
