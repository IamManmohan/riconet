package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.WmsEventName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 19/4/18. */
@Slf4j
@Service
public class WmsEventTriggerService {

  @Autowired private AppNotificationService appNotificationService;

  public void processNotification(NotificationDTO notificationDTO) {
    WmsEventName eventName = WmsEventName.valueOf(notificationDTO.getEventName());
    switch (eventName) {
      case CN_INBOUND_CLEAR:
        appNotificationService.sendTaskNotifications(notificationDTO, eventName);
        appNotificationService.sendTaskNotifications(notificationDTO, WmsEventName.IB_CLEAR);
        break;
      case TASK_CLOSED_OR_CANCELLED:
        appNotificationService.sendTaskNotifications(notificationDTO, eventName);
        appNotificationService.sendTaskNotifications(
            notificationDTO, WmsEventName.TASK_CLOSED_OR_REASSIGNED);
        break;
      case MANIFEST_CLOSED:
      case PALLET_CLOSED:
      case TASK_SUBMITTED:
      case TASK_UNASSIGNED:
      case TASK_UPSERT:
      case CN_LOADING_PLAN_UNPLAN:
      case CN_TOTAL_BOXES_CHANGE:
      case CN_UNLOADING_PLAN_UNPLAN:
        appNotificationService.sendTaskNotifications(notificationDTO, eventName);
        break;
      case SHOP_FLOOR_STATUS_UPDATE:
        appNotificationService.sendShopFloorStatusUpdateNotifications(notificationDTO);
        break;
      default:
        log.info("Event does not trigger anything {}", eventName);
    }
  }
}
