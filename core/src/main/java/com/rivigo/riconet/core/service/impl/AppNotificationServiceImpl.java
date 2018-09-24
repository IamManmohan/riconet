package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.PushNotificationConstant.BODY;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.DATA;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.HIGH;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.NOTIFICATION;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.NOTIFICATION_TYPE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.PARENT_TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TASK_ID;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TITLE;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.UIL_BODY;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.UIL_TITLE;
import static com.rivigo.zoom.common.enums.TaskType.UNLOADING_IN_LOADING;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/** Created by ashfakh on 21/09/18. */
@Service
@Slf4j
public class AppNotificationServiceImpl implements AppNotificationService {

  @Autowired private DeviceAppVersionMapperRepository deviceAppVersionMapperRepository;

  @Autowired private PushNotificationService pushNotificationService;

  @Override
  public void sendUnloadingInLoadingNotification(NotificationDTO notificationDTO) {
    Long userId =
        Long.valueOf(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.USER_ID.name()));
    Long taskId = notificationDTO.getEntityId();
    Long parentTaskId =
        Long.valueOf(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PARENT_TASK_ID.name()));
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        deviceAppVersionMapperRepository.findByUserId(userId);
    if (CollectionUtils.isEmpty(deviceAppVersionMappers)) {
      return;
    }
    JSONObject pushObject = new JSONObject();

    JSONObject notification = new JSONObject();
    notification.put(TITLE, UIL_TITLE);
    notification.put(BODY, UIL_BODY + taskId);

    JSONObject data = new JSONObject();
    data.put(NOTIFICATION_TYPE, UNLOADING_IN_LOADING);
    data.put(TASK_ID, taskId);
    data.put(PARENT_TASK_ID, parentTaskId);

    pushObject.put(NOTIFICATION, notification);
    pushObject.put(DATA, data);
    deviceAppVersionMappers.forEach(
        d -> {
          try {
            pushNotificationService.send(pushObject, d.getFirebaseToken(), HIGH);
          } catch (IOException e) {
            log.error("Error sending push notification {}", e);
          }
        });
  }
}
