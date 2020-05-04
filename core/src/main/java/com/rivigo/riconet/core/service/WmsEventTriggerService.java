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

  @Autowired private RTOService rtoService;

  public void processNotification(NotificationDTO notificationDTO) {
    WmsEventName eventName = WmsEventName.valueOf(notificationDTO.getEventName());
    switch (eventName) {
      case CN_INBOUND_CLEAR:
      case TASK_CLOSED_OR_CANCELLED:
      case CN_REMOVED_FROM_UNLOADING:
      case MANIFEST_CLOSED:
      case PALLET_CLOSED:
      case TASK_SUBMITTED_ANOTHER_USER:
      case TASK_UNASSIGNED:
      case TASK_UPSERT:
      case CN_LOADING_PLAN_UNPLAN:
      case CN_TOTAL_BOXES_CHANGE:
        appNotificationService.sendTaskNotifications(notificationDTO, eventName);
        break;
      case SHOP_FLOOR_STATUS_UPDATE:
        appNotificationService.sendShopFloorStatusUpdateNotifications(notificationDTO);
        break;
      case TASK_CLOSED:
        rtoService.processTaskClosedEvent(notificationDTO);
        break;
      default:
        log.info("Event does not trigger anything {}", eventName);
    }
  }
}
