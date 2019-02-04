package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.PushNotificationConstant.DATA;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.ENTITY_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.HIGH;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.NOTIFICATION_TYPE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARENT_TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TIME_STAMP;
import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAULT_APP_USER_IDS;
import static com.rivigo.zoom.common.enums.TaskType.UNLOADING_IN_LOADING;

import com.google.common.collect.ImmutableMap;
import com.rivigo.riconet.core.constants.PushNotificationConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TaskDto;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private RestClientUtilityService restClientUtilityService;

  @Autowired private LocationService locationService;

  @Autowired private UserMasterService userMasterService;

  @Override
  public void sendUnloadingInLoadingNotification(NotificationDTO notificationDTO) {
    // Should be in sync with user table in backen
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
    sendNotification(pushObject, userId);
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
      sendNotification(pushObject, user.getId());
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
      sendNotification(pushObject, user.getId());
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
    sendNotification(pushObject, userId);
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
              HttpEntity<?> entity = new HttpEntity<>(restClientUtilityService.getHeaders());
              return restClientUtilityService.executeRest(
                  restClientUtilityService.buildUrlWithParams(
                      UrlConstant.WMS_TASK_BY_TRIP_LOCATION_AND_TYPE,
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
                sendNotification(pushObject, user.getId());
              }
            });
  }

  private void sendNotification(JSONObject notificationPayload, Long userId) {
    List<DeviceAppVersionMapper> deviceAppVersionMappers;
    if (!"production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      List<Long> userIdList =
          Arrays.stream(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57").split(","))
              .map(Long::valueOf)
              .collect(Collectors.toList());
      log.info("Staging server. sending notification to user {}", userId);
      deviceAppVersionMappers = deviceAppVersionMapperRepository.findByUserIdIn(userIdList);
    } else {
      deviceAppVersionMappers = deviceAppVersionMapperRepository.findByUserId(userId);
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
