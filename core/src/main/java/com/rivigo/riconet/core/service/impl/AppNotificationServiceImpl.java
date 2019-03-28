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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name()));

    Long pickUpId =
        Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name()));

    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());

    Long pickUpCaptainNumber =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name()));
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // populate data.
    // put pickup id and captain number.
    data.put(PICKUP_ID, pickUpId);
    data.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    data.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    pushObject.put(DATA, data);

    sendNotification(pushObject, pickUpCreatorUserId, ApplicationId.retail_app);
  }

  @Override
  public void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO) {

    Long pickUpCreatorUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name()));

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // put pickupId and captain number

    Long pickUpId =
        Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICK_UP_ID.name()));

    Long pickUpCaptainNumber =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name()));

    String pickUpCaptainName =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name());

    data.put(PICKUP_ID, pickUpId);
    data.put(PICKUP_CAPTAIN_NAME, pickUpCaptainName);
    data.put(PICKUP_CAPTAIN_NUMBER, pickUpCaptainNumber);
    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());

    pushObject.put(DATA, data);
    sendNotification(pushObject, pickUpCreatorUserId, ApplicationId.retail_app);
  }

  @Override
  public void sendCnLoadedEvent(NotificationDTO notificationDTO) {

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    // cnote and consignor/consignee.
    Long consignorUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name()));
    Long consigneeUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name()));

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);

    pushObject.put(DATA, data);
    sendNotification(pushObject, consigneeUserId, ApplicationId.retail_app);
    sendNotification(pushObject, consignorUserId, ApplicationId.retail_app);
  }

  @Override
  public void sendCnOutForDelivery(NotificationDTO notificationDTO) {

    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
    Long consigneeUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name()));

    String cnoteType =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE_TYPE.name());
    String paymentMode =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PAYMENT_MODE.name());

    boolean isToPay = false;
    if (cnoteType != null
        && paymentMode != null
        && CnoteType.RETAIL.name().equals(cnote)
        && PaymentMode.TO_PAY.name().equals(paymentMode)) isToPay = true;

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);

    data.put(IS_TOPAY, "FALSE");
    if (isToPay) data.put(IS_TOPAY, "TRUE");

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

    pushObject.put(DATA, data);
    sendNotification(pushObject, consigneeUserId, ApplicationId.retail_app);
  }

  @Override
  public void sendCnDeliveredNotification(NotificationDTO notificationDTO) {
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());

    Long consignorUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name()));
    Long consigneeUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name()));

    data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
    data.put(CNOTE, cnote);
    // add is cn delivery delayed.
    data.put(IS_CN_DELIVERY_DELAYED, "FALSE");

    Long promisedDeliveryDateTime =
        Optional.of(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.CpbSummary.PROMISED_DELIVERY_DATE_TIME.name()))
            .map(Long::valueOf)
            .orElse(null);
    Long deliveryDateTime =
        Optional.of(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name()))
            .map(Long::valueOf)
            .orElse(null);

    if (deliveryDateTime != null
        && promisedDeliveryDateTime != null
        && deliveryDateTime > promisedDeliveryDateTime) data.put(IS_CN_DELIVERY_DELAYED, "TRUE");
    pushObject.put(DATA, data);

    sendNotification(pushObject, consigneeUserId, ApplicationId.retail_app);
    sendNotification(pushObject, consignorUserId, ApplicationId.retail_app);
  }

  @Override
  public void sendCnDelayEvent(NotificationDTO notificationDTO) {
    JSONObject pushObject = new JSONObject();
    JSONObject data = new JSONObject();

    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());

    Long consignorUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name()));
    Long consigneeUserId =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name()));

    Long eventOccurredTime =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(
                    ZoomCommunicationFieldNames.ConsignmentEventDelayNotification
                        .EVENT_OCCURRED_TIME.name()));

    Long eventCutOffTime =
        Long.valueOf(
            notificationDTO
                .getMetadata()
                .get(
                    ZoomCommunicationFieldNames.ConsignmentEventDelayNotification.EVENT_CUT_OFF_TIME
                        .name()));

    if (eventCutOffTime != null
        && eventOccurredTime != null
        && eventCutOffTime < eventOccurredTime) {

      data.put(NOTIFICATION_TYPE, notificationDTO.getEventName());
      data.put(CNOTE, cnote);

      pushObject.put(DATA, data);

      sendNotification(pushObject, consigneeUserId, ApplicationId.retail_app);
      sendNotification(pushObject, consignorUserId, ApplicationId.retail_app);
    }
  }

  private void sendNotification(JSONObject notificationPayload, Long userId, ApplicationId appId) {
    if (appId.equals("retail_app"))
      log.info("the notification payload is {} and user id is {}", notificationPayload, userId);
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
            pushNotificationService.send(notificationPayload, d.getFirebaseToken(), HIGH);
          } catch (IOException e) {
            log.error("Error sending push notification {}", e);
          }
        });
  }
}
