package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.PushNotificationConstant.CNOTE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.DATA;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.ENTITY_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.HIGH;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.IS_CN_DELIVERY_DELAYED;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.IS_TOPAY;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.NOTIFICATION_TYPE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.ONLINE_PAYMENT_LINK;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.OU_CODE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARENT_TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARTNER_MOBILE_NUMBER;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARTNER_NAME;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_CAPTAIN_NAME;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_CAPTAIN_NUMBER;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.SHOP_FLOOR_ENABLED;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TIME_STAMP;
import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAULT_APP_USER_IDS;

import com.google.common.collect.ImmutableMap;
import com.rivigo.riconet.core.constants.ExpressAppConstants;
import com.rivigo.riconet.core.constants.PushNotificationConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TaskDto;
import com.rivigo.riconet.core.enums.WmsEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ApplicationId;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/** Created by ashfakh on 21/09/18. */
@Service
@Slf4j
public class AppNotificationServiceImpl implements AppNotificationService {

  @Autowired private DeviceAppVersionMapperRepository deviceAppVersionMapperRepository;

  @Autowired private PushNotificationService pushNotificationService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired
  @Qualifier("datastoreRestClientUtilityServiceImpl")
  private RestClientUtilityService datastoreRestService;

  @Autowired private LocationService locationService;

  @Autowired private UserMasterService userMasterService;

  @Value("${zoom.wms.url}")
  private String zoomWmsUrl;

  @Override
  public void sendTaskUpsertNotification(NotificationDTO notificationDTO) {
    // Should be in sync with user table in backend
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
    Long taskId = notificationDTO.getEntityId();
    Long parentTaskId = null;
    if (notificationDTO
        .getMetadata()
        .containsKey(ZoomCommunicationFieldNames.PARENT_TASK_ID.name())) {
      parentTaskId =
          Long.valueOf(
              notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PARENT_TASK_ID.name()));
    }

    JSONObject pushObject = new JSONObject();

    JSONObject data = new JSONObject();
    data.put(NOTIFICATION_TYPE, WmsEventName.TASK_UPSERT.name());
    data.put(TASK_ID, taskId);
    data.put(PARENT_TASK_ID, parentTaskId);
    data.put(TIME_STAMP, notificationDTO.getTsMs());

    pushObject.put(DATA, data);
    sendNotification(pushObject, userId, ApplicationId.scan_app);
  }

  @Override
  public void sendShopFloorStatusUpdateNotifications(NotificationDTO notificationDTO) {
    String ouCode = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.OU_CODE.name());
    Boolean shopFloorEnabled =
        Boolean.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.SHOP_FLOOR_ENABLED.name()));

    JSONObject pushObject = new JSONObject();

    JSONObject data = new JSONObject();
    data.put(NOTIFICATION_TYPE, WmsEventName.SHOP_FLOOR_STATUS_UPDATE.name());
    data.put(OU_CODE, ouCode);
    data.put(SHOP_FLOOR_ENABLED, shopFloorEnabled);
    data.put(TIME_STAMP, notificationDTO.getTsMs());

    pushObject.put(DATA, data);
    // TODO: Uncomment me
    //    Location locationByCode = locationService.getLocationByCode(ouCode);
    //    List<DeviceAppVersionMapper> deviceAppVersionMapperList =
    //        deviceAppVersionMapperRepository.findByAppIdAndLocationId(
    //            ApplicationId.scan_app.name(), locationByCode.getId());
    //    sendNotification(pushObject, deviceAppVersionMapperList, ApplicationId.scan_app);
  }

  @Override
  public void sendLoadingUnloadingNotification(NotificationDTO notificationDTO) {
    String userEmail =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_EMAIL.name());
    Long taskId = notificationDTO.getEntityId();

    TaskType taskType =
        TaskType.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.TASK_TYPE.name()));

    JSONObject pushObject = new JSONObject();

    JSONObject data = new JSONObject();
    data.put(NOTIFICATION_TYPE, taskType.name());
    data.put(TASK_ID, taskId);
    data.put(TIME_STAMP, notificationDTO.getTsMs());

    pushObject.put(DATA, data);
    User user = userMasterService.getByEmail(userEmail);
    if (user != null) {
      sendNotification(pushObject, user.getId(), ApplicationId.scan_app);
    }
  }

  @Override
  public void sendPalletClosedNotification(NotificationDTO notificationDTO) {
    String userEmail =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_EMAIL.name());
    Long palletId = notificationDTO.getEntityId();
    Long taskId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.TASK_ID.name()));
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    data.put(ENTITY_ID, palletId);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(TASK_ID, taskId);

    pushObject.put(DATA, data);
    User user = userMasterService.getByEmail(userEmail);
    if (user != null) {
      sendNotification(pushObject, user.getId(), ApplicationId.scan_app);
    }
  }

  @Override
  public void sendTaskClosedOrReassignedNotification(NotificationDTO notificationDTO) {
    // Should be in sync with user table in backend
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
    Long taskId = notificationDTO.getEntityId();
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(TASK_ID, taskId);

    pushObject.put(DATA, data);
    sendNotification(pushObject, userId, ApplicationId.scan_app);
  }

  @Override
  public void sendIBClearEvent(NotificationDTO notificationDTO) {
    Long locationId =
        Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.LOCATION_ID.name()));
    consignmentScheduleService
        .getCacheForConsignmentAtLocation(notificationDTO.getEntityId(), locationId)
        .flatMap(
            cache -> {
              Location location = locationService.getLocationById(locationId);
              HttpEntity<?> entity = new HttpEntity<>(datastoreRestService.getHeaders());
              return datastoreRestService.executeRest(
                  datastoreRestService.buildUrlWithParams(
                      zoomWmsUrl + UrlConstant.WMS_TASK_BY_TRIP_LOCATION_AND_TYPE,
                      ImmutableMap.of(
                          "tripId",
                          String.valueOf(cache.getTripId()),
                          "tripType",
                          String.valueOf(cache.getTripType()),
                          "locationCode",
                          location.getCode(),
                          "taskType",
                          String.valueOf(TaskType.LOADING))),
                  HttpMethod.GET,
                  entity,
                  TaskDto.class);
            })
        .ifPresent(
            taskDto -> {
              log.info("Sending ib clear event for {}", notificationDTO.getEntityId());
              JSONObject pushObject = new JSONObject();
              JSONObject data = new JSONObject();

              data.put(NOTIFICATION_TYPE, PushNotificationConstant.IB_CLEAR_EVENT);
              data.put(TASK_ID, taskDto.getId());

              pushObject.put(DATA, data);
              User user = userMasterService.getByEmail(taskDto.getUserEmail());
              if (user != null) {
                sendNotification(pushObject, user.getId(), ApplicationId.scan_app);
              }
            });
  }

  //Paused Express app notification
  @Override
  public void sendPickUpAssignmentEvent(NotificationDTO notificationDTO) {

    Long pickUpCreatorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name());

    Long pickUpId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name());
    if (pickUpId == null) {
      log.warn("Cannot send notification when pickup id is null");
      return;
    }

    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());
    if (pickUpCaptainName == null) {
      log.warn("Cannot send notification when captain name is null");
      return;
    }

    Long pickUpCaptainNumber =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name());
    if (pickUpCaptainNumber == null) {
      log.warn("Cannot send notification when captain number is null");
      return;
    }
    // populate data.
    // put pickup id and captain number.
    JSONObject notificationData = new JSONObject();
    notificationData.put(PICKUP_ID, pickUpId);
    notificationData.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    notificationData.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(
        ExpressAppConstants.NotificationKey.IDENTIFIER,
        ExpressAppConstants.NotificationIdentifier.PICKUP_ASSIGNED);
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    JSONObject notification = new JSONObject();
    notification.put(
        ExpressAppConstants.NotificationKey.BODY,
        ExpressAppConstants.NotificationBody.PICKUP_ASSIGNED);
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.PICKUP_ASSIGNED);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        pickUpCreatorUserId,
        ApplicationId.retail_app);
  }

  private JSONObject getJsonObjectForRetailApp(
      JSONObject notificationData, JSONObject notification) {
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();
    data.put(ExpressAppConstants.NotificationKey.NOTIFICATION_DATA, notificationData);
    data.put(ExpressAppConstants.NotificationKey.NOTIFICATION, notification);
    pushObject.put(DATA, data);
    return pushObject;
  }

  //Paused Express app notification
  @Override
  public void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO) {

    Long pickUpCreatorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name());

    // put pickupId and captain number

    Long pickUpId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name());
    if (pickUpId == null) {
      log.warn("Cannot send notification when pickup id is null");
      return;
    }
    Long pickUpCaptainNumber =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name());
    if (pickUpCaptainNumber == null) {
      log.warn("Cannot send notification when captain number is null");
      return;
    }
    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());
    if (pickUpCaptainName == null) {
      log.warn("Cannot send notification when captain name is null");
      return;
    }
    JSONObject notificationData = new JSONObject();
    notificationData.put(PICKUP_ID, pickUpId);
    notificationData.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    notificationData.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(
        ExpressAppConstants.NotificationKey.IDENTIFIER,
        ExpressAppConstants.NotificationIdentifier.PICKUP_REACHED_AT_CLIENT_WAREHOUSE);
    JSONObject notification = new JSONObject();
    notification.put(
        ExpressAppConstants.NotificationKey.BODY,
        ExpressAppConstants.NotificationBody.PICKUP_REACHED_AT_CLIENT_WAREHOUSE);
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.PICKUP_REACHED_AT_CLIENT_WAREHOUSE);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        pickUpCreatorUserId,
        ApplicationId.retail_app);
  }

  //Paused Express app notification
  @Override
  public void sendPickupCancellationNotification(NotificationDTO notificationDTO) {
    Long pickUpCreatorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name());
    JSONObject notificationData = new JSONObject();
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(
        ExpressAppConstants.NotificationKey.IDENTIFIER,
        ExpressAppConstants.NotificationIdentifier.PICKUP_CANCELLATION);
    JSONObject notification = new JSONObject();
    notification.put(
        ExpressAppConstants.NotificationKey.BODY,
        ExpressAppConstants.NotificationBody.PICKUP_CANCELLATION);
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.PICKUP_CANCELLATION);
    log.info(
        "Calling send notification for Pickup :{} cancellation", notificationDTO.getEntityId());
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        pickUpCreatorUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnFirstOuDispatchNotification(NotificationDTO notificationDTO) {
    // cnote and consignor/consignee.
    Long consignorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name());
    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    if (cnote == null) {
      log.warn("Cannot send notification when cnote is null");
      return;
    }
    JSONObject notificationData = new JSONObject();
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(CNOTE, cnote);
    notificationData.put(
        ExpressAppConstants.NotificationKey.IDENTIFIER,
        ExpressAppConstants.NotificationIdentifier.CN_FIRST_OU_DISPATCH);
    JSONObject notification = new JSONObject();
    notification.put(
        ExpressAppConstants.NotificationKey.BODY,
        String.format(ExpressAppConstants.NotificationBody.CN_FIRST_OU_DISPATCH, cnote));
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.CN_FIRST_OU_DISPATCH);
    notification.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(ExpressAppConstants.PageUrl.BOOKING)
            .toString());
    List<JSONObject> actions = new ArrayList<>();
    JSONObject trackObject = new JSONObject();
    trackObject.put(
        ExpressAppConstants.NotificationKey.TITLE, ExpressAppConstants.NotificationAction.TRACK);
    trackObject.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(String.format(ExpressAppConstants.PageUrl.TRACK_CN, cnote))
            .toString());
    actions.add(trackObject);
    notification.put(ExpressAppConstants.NotificationKey.ACTIONS, actions);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consigneeUserId,
        ApplicationId.retail_app);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consignorUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnDrsDispatchEvent(NotificationDTO notificationDTO) {
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    if (cnote == null) {
      log.warn("Cannot send notification when cnote is null");
      return;
    }
    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());
    String cnoteType =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE_TYPE.name());
    String paymentMode =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PAYMENT_MODE.name());
    boolean isToPay = false;
    if (cnoteType != null
        && paymentMode != null
        && CnoteType.RETAIL.name().equals(cnoteType)
        && PaymentMode.TO_PAY.name().equals(paymentMode)) isToPay = true;
    JSONObject notificationData = new JSONObject();
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(CNOTE, cnote);
    if (isToPay) {
      notificationData.put(IS_TOPAY, "TRUE");
      notificationData.put(
          ExpressAppConstants.NotificationKey.IDENTIFIER,
          ExpressAppConstants.NotificationIdentifier.TO_PAY_CN_OUT_FOR_DELIVERY);
    } else {
      notificationData.put(IS_TOPAY, "FALSE");
      notificationData.put(
          ExpressAppConstants.NotificationKey.IDENTIFIER,
          ExpressAppConstants.NotificationIdentifier.PAID_CN_OUT_FOR_DELIVERY);
    }
    // put captain's number.
    String tpmCaptainPhoneNumber =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.MOBILE_NO.name());
    String tpmCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.NAME.name());
    String onlinePaymentLink =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.ONLINE_PAYMENT_LINK.name());
    if (onlinePaymentLink != null) notificationData.put(ONLINE_PAYMENT_LINK, onlinePaymentLink);
    JSONObject notification = new JSONObject();
    notificationData.put(PARTNER_MOBILE_NUMBER, tpmCaptainPhoneNumber);
    notificationData.put(PARTNER_NAME, tpmCaptainName);
    StringBuilder sb = new StringBuilder();
    sb.append(String.format(ExpressAppConstants.NotificationBody.CN_DRS_DISPATCHED, cnote));
    if (isToPay && onlinePaymentLink != null) {
      sb.append("Make your payment now ");
    }
    notification.put(ExpressAppConstants.NotificationKey.BODY, sb.toString());
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.CN_DRS_DISPATCHED);
    notification.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(ExpressAppConstants.PageUrl.BOOKING)
            .toString());
    List<JSONObject> actions = new ArrayList<>();
    JSONObject trackObject = new JSONObject();
    trackObject.put(
        ExpressAppConstants.NotificationKey.TITLE, ExpressAppConstants.NotificationAction.TRACK);
    trackObject.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(String.format(ExpressAppConstants.PageUrl.TRACK_CN, cnote))
            .toString());
    actions.add(trackObject);
    JSONObject callObject = new JSONObject();
    callObject.put(
        ExpressAppConstants.NotificationKey.TITLE, ExpressAppConstants.NotificationAction.CALL);
    callObject.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(String.format(ExpressAppConstants.PageUrl.CALL, tpmCaptainPhoneNumber))
            .toString());
    actions.add(callObject);
    notification.put(ExpressAppConstants.NotificationKey.ACTIONS, actions);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consigneeUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnDeliveredNotification(NotificationDTO notificationDTO) {
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    if (cnote == null) {
      log.warn("Cannot send notification when cnote is null");
      return;
    }
    Long consignorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name());
    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());
    JSONObject notificationData = new JSONObject();
    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(CNOTE, cnote);
    Long promisedDeliveryDateTime =
        getFieldAsLongFromNotificationDto(
            notificationDTO,
            ZoomCommunicationFieldNames.CpbSummary.PROMISED_DELIVERY_DATE_TIME.name());
    Long deliveryDateTime =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name());
    StringBuilder sb = new StringBuilder();
    sb.append("Your shipment ");
    sb.append(cnote);
    sb.append(" has been delivered");
    if (deliveryDateTime != null
        && promisedDeliveryDateTime != null
        && deliveryDateTime > promisedDeliveryDateTime) {
      notificationData.put(IS_CN_DELIVERY_DELAYED, "TRUE");
      sb.append("! We sincerely apologize for any inconvenience due to the delay");
      notificationData.put(ExpressAppConstants.NotificationKey.IDENTIFIER, "DELIVERED_DELAYED");
    } else {
      notificationData.put(IS_CN_DELIVERY_DELAYED, "FALSE");
      sb.append(" on time");
      notificationData.put(ExpressAppConstants.NotificationKey.IDENTIFIER, "DELIVERED");
    }
    JSONObject notification = new JSONObject();
    notification.put(ExpressAppConstants.NotificationKey.BODY, sb.toString());
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.CN_DELIVERED);
    notification.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(ExpressAppConstants.PageUrl.POD)
            .toString());
    List<JSONObject> actions = new ArrayList<>();
    JSONObject trackObject = new JSONObject();
    trackObject.put(
        ExpressAppConstants.NotificationKey.TITLE, ExpressAppConstants.NotificationAction.POD);
    trackObject.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(String.format(ExpressAppConstants.PageUrl.TRACK_POD, cnote))
            .toString());
    actions.add(trackObject);
    notification.put(ExpressAppConstants.NotificationKey.ACTIONS, actions);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consigneeUserId,
        ApplicationId.retail_app);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consignorUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnDelayedNotification(NotificationDTO notificationDTO) {
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    if (cnote == null) {
      log.warn("Cannot send notification when cnote is null");
      return;
    }

    JSONObject notificationData = new JSONObject();
    JSONObject notification = new JSONObject();

    Long consignorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name());

    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());

    notificationData.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    notificationData.put(CNOTE, cnote);

    notificationData.put(
        ExpressAppConstants.NotificationKey.IDENTIFIER,
        ExpressAppConstants.NotificationIdentifier.CN_DELAYED);

    notification.put(
        ExpressAppConstants.NotificationKey.BODY,
        String.format(ExpressAppConstants.NotificationBody.CN_DELAYED, cnote));
    notification.put(
        ExpressAppConstants.NotificationKey.TITLE,
        ExpressAppConstants.NotificationTitle.CN_DELAYED);

    notification.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(ExpressAppConstants.PageUrl.BOOKING)
            .toString());
    List<JSONObject> actions = new ArrayList<>();
    JSONObject trackObject = new JSONObject();
    trackObject.put(
        ExpressAppConstants.NotificationKey.TITLE, ExpressAppConstants.NotificationAction.TRACK);
    trackObject.put(
        ExpressAppConstants.NotificationKey.URL,
        new StringBuilder()
            .append(ExpressAppConstants.PageUrl.APP_URL)
            .append(String.format(ExpressAppConstants.PageUrl.TRACK_CN, cnote))
            .toString());
    actions.add(trackObject);
    notification.put(ExpressAppConstants.NotificationKey.ACTIONS, actions);

    log.info("CN DELAYED: {} {}", notificationData.toString(), notification.toString());

    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consigneeUserId,
        ApplicationId.retail_app);
    sendNotification(
        getJsonObjectForRetailApp(notificationData, notification),
        consignorUserId,
        ApplicationId.retail_app);
  }

  private Long getFieldAsLongFromNotificationDto(
      NotificationDTO notificationDTO, @NonNull String field) {
    try {
      return Long.valueOf(notificationDTO.getMetadata().get(field));
    } catch (Exception e) {
      log.error(
          "An exception:{} occurred while getting filed: {} from notificationDTO: {}",
          e,
          field,
          notificationDTO);
      return null;
    }
  }

  private void sendNotification(JSONObject notificationPayload, Long userId, ApplicationId appId) {
    if (userId == null) {
      log.warn("Cannot send notification if userId is null");
      return;
    }
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        deviceAppVersionMapperRepository.findByUserIdAndAppId(userId, appId);
    sendNotification(notificationPayload, deviceAppVersionMappers, appId);
  }

  private void sendNotification(
      JSONObject notificationPayload,
      List<DeviceAppVersionMapper> deviceAppVersionMappers,
      ApplicationId appId) {

    if (!"production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      List<Long> userIdList =
          Arrays.stream(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57").split(","))
              .map(Long::valueOf)
              .collect(Collectors.toList());

      deviceAppVersionMappers.addAll(
          deviceAppVersionMapperRepository.findByUserIdInAndAppId(userIdList, appId));
      log.info(
          "Staging server. Sending notification for users {}",
          deviceAppVersionMappers
              .stream()
              .map(DeviceAppVersionMapper::getUserId)
              .collect(Collectors.toSet()));
    }
    if (CollectionUtils.isEmpty(deviceAppVersionMappers)) {
      log.info("No device registered to the user. Not sending notifications.");
      return;
    }
    deviceAppVersionMappers
        .stream()
        .filter(d -> d.getFirebaseToken() != null)
        .forEach(
            d -> {
              try {
                pushNotificationService.send(
                    notificationPayload, d.getFirebaseToken(), HIGH, appId);
              } catch (IOException e) {
                log.error("Error sending push notification {}", e.getMessage(), e);
              }
            });
  }
}
