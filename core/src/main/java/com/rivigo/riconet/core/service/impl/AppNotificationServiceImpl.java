package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.PushNotificationConstant.CNOTE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.DATA;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.ENTITY_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.HIGH;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.IS_CN_DELIVERY_DELAYED;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.IS_TOPAY;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.NOTIFICATION_TYPE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.ONLINE_PAYMENT_LINK;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARENT_TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARTNER_MOBILE_NUMBER;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARTNER_NAME;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_CAPTAIN_NAME;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_CAPTAIN_NUMBER;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PICKUP_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TIME_STAMP;
import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAULT_APP_USER_IDS;
import static com.rivigo.zoom.common.enums.TaskType.UNLOADING_IN_LOADING;

import com.rivigo.riconet.core.constants.PushNotificationConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ApplicationId;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.TaskStatus;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import com.rivigo.zoom.common.repository.mysql.OATaskAssignmentRepository;
import com.rivigo.zoom.common.repository.mysql.retail_app.CnDelayNotificationRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
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

  @Autowired private OATaskAssignmentRepository oaTaskAssignmentRepository;

  @Autowired private CnDelayNotificationRepository cnDelayNotificationRepository;

  @Override
  public void sendUnloadingInLoadingNotification(NotificationDTO notificationDTO) {
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
    Long taskId = notificationDTO.getEntityId();
    Long parentTaskId =
        Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PARENT_TASK_ID.name()));

    JSONObject pushObject = new JSONObject();

    JSONObject data = new JSONObject();
    data.put(NOTIFICATION_TYPE, UNLOADING_IN_LOADING.name());
    data.put(TASK_ID, taskId);
    data.put(PARENT_TASK_ID, parentTaskId);
    data.put(TIME_STAMP, notificationDTO.getTsMs());

    pushObject.put(DATA, data);
    sendNotification(pushObject, userId, ApplicationId.scan_app);
  }

  @Override
  public void sendLoadingUnloadingNotification(NotificationDTO notificationDTO) {
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
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
    sendNotification(pushObject, userId, ApplicationId.scan_app);
  }

  @Override
  public void sendPalletClosedNotification(NotificationDTO notificationDTO) {
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
    Long palletId = notificationDTO.getEntityId();
    Long taskId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.TASK_ID.name()));
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    data.put(ENTITY_ID, palletId);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(TASK_ID, taskId);

    pushObject.put(DATA, data);
    sendNotification(pushObject, userId, ApplicationId.scan_app);
  }

  @Override
  public void sendTaskClosedOrReassignedNotification(NotificationDTO notificationDTO) {
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
        .map(
            cache ->
                oaTaskAssignmentRepository
                    .findByTripIdAndTripTypeAndLocationIdAndTaskTypeAndStatusInAndIsActiveTrue(
                        cache.getTripId(),
                        cache.getTripType(),
                        locationId,
                        TaskType.LOADING,
                        Arrays.asList(TaskStatus.OPEN, TaskStatus.IN_PROGRESS, TaskStatus.PAUSED)))
        .ifPresent(
            oaTask -> {
              log.info("Sending ib clear event for {}", notificationDTO.getEntityId());
              Long userId = oaTask.getUserId();
              Long taskId = oaTask.getId();
              JSONObject pushObject = new JSONObject();
              JSONObject data = new JSONObject();

              data.put(NOTIFICATION_TYPE, PushNotificationConstant.IB_CLEAR_EVENT);
              data.put(TASK_ID, taskId);

              pushObject.put(DATA, data);
              sendNotification(pushObject, userId, ApplicationId.scan_app);
            });
  }

  @Override
  public void sendPickUpAssignmentEvent(NotificationDTO notificationDTO) {

    Long pickUpCreatorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name());

    Long pickUpId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name());

    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());

    Long pickUpCaptainNumber =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name());

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // populate data.
    // put pickup id and captain number.
    data.put(PICKUP_ID, pickUpId);
    data.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    data.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put("identifier", "PICKUP_ASSIGNED");

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    pushObject.put(DATA, data);

    JSONObject notificationBodyAndTitle = new JSONObject();
    String title = "Your pickup partner has been assigned for the pickup " + pickUpId.toString();
    notificationBodyAndTitle.put("body", title);
    notificationBodyAndTitle.put("title", "Partner assigned");

    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        pickUpCreatorUserId,
        ApplicationId.retail_app);
  }

  private JSONObject getJsonObjectForRetailApp(
      JSONObject pushObject, JSONObject notificationBodyAndTitle) {
    pushObject.put("notification", notificationBodyAndTitle);
    return pushObject;
  }

  @Override
  public void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO) {

    Long pickUpCreatorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name());

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // put pickupId and captain number

    Long pickUpId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name());

    Long pickUpCaptainNumber =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name());

    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());

    data.put(PICKUP_ID, pickUpId);
    data.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    data.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put("identifier", "PICKUP_REACHED_AT_CLIENT_WAREHOUSE");

    pushObject.put(DATA, data);

    JSONObject notificationBodyAndTitle = new JSONObject();

    String title = "Knock, knock! We have reached your location for pickup " + pickUpId.toString();
    notificationBodyAndTitle.put("body", title);
    notificationBodyAndTitle.put("title", "Partner reached");

    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        pickUpCreatorUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnLoadedEvent(NotificationDTO notificationDTO) {

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // cnote and consignor/consignee.
    Long consignorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name());
    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);
    data.put("identifier", "DISPATCHED");

    JSONObject notificationBodyAndTitle = new JSONObject();

    String title = "Your shipment " + cnote + " has been dispatched!";
    notificationBodyAndTitle.put("body", title);
    notificationBodyAndTitle.put("title", "Dispatched");

    pushObject.put(DATA, data);

    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        consigneeUserId,
        ApplicationId.retail_app);
    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        consignorUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnDrsDispatchEvent(NotificationDTO notificationDTO) {

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
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

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);

    if (isToPay) {
      data.put(IS_TOPAY, "TRUE");
      data.put("identifier", "OUT_FOR_DELIVERY_TO_PAY");
    } else {
      data.put(IS_TOPAY, "FALSE");
      data.put("identifier", "OUT_FOR_DELIVERY_PAID");
    }

    // put captain's number.
    String tpmCaptainPhoneNumber =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.MOBILE_NO.name());
    String tpmCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.NAME.name());
    String onlinePaymentLink =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.ONLINE_PAYMENT_LINK.name());

    if (onlinePaymentLink != null) data.put(ONLINE_PAYMENT_LINK, onlinePaymentLink);

    data.put(PARTNER_MOBILE_NUMBER, tpmCaptainPhoneNumber);
    data.put(PARTNER_NAME, tpmCaptainName);

    String title = "Your shipment " + cnote + " is out for delivery! ";
    if (isToPay && onlinePaymentLink != null) title = title + "Make your payment now ";

    JSONObject notificationBodyAndTitle = new JSONObject();
    notificationBodyAndTitle.put("body", title);
    notificationBodyAndTitle.put("title", "Out for delivery! ");

    pushObject.put(DATA, data);
    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        consigneeUserId,
        ApplicationId.retail_app);
  }

  @Override
  public void sendCnDeliveredNotification(NotificationDTO notificationDTO) {
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());

    Long consignorUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name());

    Long consigneeUserId =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name());

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);

    Long promisedDeliveryDateTime =
        getFieldAsLongFromNotificationDto(
            notificationDTO,
            ZoomCommunicationFieldNames.CpbSummary.PROMISED_DELIVERY_DATE_TIME.name());

    Long deliveryDateTime =
        getFieldAsLongFromNotificationDto(
            notificationDTO, ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name());

    String title = "Your shipment " + cnote + " has been delivered";

    if (deliveryDateTime != null
        && promisedDeliveryDateTime != null
        && deliveryDateTime > promisedDeliveryDateTime) {
      data.put(IS_CN_DELIVERY_DELAYED, "TRUE");
      title = title + "! We sincerely apologize for any inconvenience due to the delay";
      data.put("identifier", "DELIVERED_DELAYED");
    } else {
      data.put(IS_CN_DELIVERY_DELAYED, "FALSE");
      title = title + " on time";
      data.put("identifier", "DELIVERED");
    }

    JSONObject notificationBodyAndTitle = new JSONObject();
    notificationBodyAndTitle.put("body", title);
    notificationBodyAndTitle.put("title", "Delivered");

    pushObject.put(DATA, data);
    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        consigneeUserId,
        ApplicationId.retail_app);
    sendNotification(
        getJsonObjectForRetailApp(pushObject, notificationBodyAndTitle),
        consignorUserId,
        ApplicationId.retail_app);
  }

  private Long getFieldAsLongFromNotificationDto(
      NotificationDTO notificationDTO, @NonNull String field) {
    try {
      return Long.valueOf(notificationDTO.getMetadata().get(field));
    } catch (Exception e) {
      log.info(
          "An exception:{} occurred while getting filed: {} from notificationDTO: {}",
          e,
          field,
          notificationDTO);
      return null;
    }
  }

  private void sendNotification(JSONObject notificationPayload, Long userId, ApplicationId appId) {
    if (ApplicationId.retail_app.equals(appId))
      log.debug(
          "################################################the notification payload is {} and user id is {}",
          notificationPayload,
          userId);
    if (userId == null) {
      log.info("Cannot send notification if userId is null");
      return;
    }
    List<DeviceAppVersionMapper> deviceAppVersionMappers;
    if (!"production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      List<Long> userIdList =
          Arrays.stream(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57").split(","))
              .map(Long::valueOf)
              .collect(Collectors.toList());
      log.info("Staging server. sending notification to user {}", userId);
      userIdList.add(userId);
      deviceAppVersionMappers =
          deviceAppVersionMapperRepository.findByUserIdInAndAppId(userIdList, appId);
    } else {
      deviceAppVersionMappers =
          deviceAppVersionMapperRepository.findByUserIdAndAppId(userId, appId);
    }
    if (CollectionUtils.isEmpty(deviceAppVersionMappers)) {
      log.info("No device registered to the user. Not sending notifications.");
      return;
    }
    deviceAppVersionMappers.forEach(
        d -> {
          try {
            pushNotificationService.send(notificationPayload, d.getFirebaseToken(), HIGH, appId);
          } catch (IOException e) {
            log.error("Error sending push notification {}", e);
          }
        });
  }
}
