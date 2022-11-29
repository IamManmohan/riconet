package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.WmsEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 19/4/18. */
@Slf4j
@Service
public class WmsEventTriggerService {

  @Autowired private AppNotificationService appNotificationService;

  @Autowired private RTOService rtoService;

  @Autowired private TicketingClientService ticketingClientService;

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
      case RTO_REVERSE_TASK_OPEN:
        String entityId =
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        String reverseCnote =
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.REVERSE_CNOTE.toString());
        if (entityId != null && reverseCnote != null) {
          Map<String, String> metadata =
              Collections.singletonMap(
                  ZoomCommunicationFieldNames.REVERSE_CNOTE.toString(), reverseCnote);
          ticketingClientService.autoCloseTicketWithMetaData(
              entityId, TicketEntityType.CN.name(), eventName.name(), metadata);
        }
        break;
      default:
        log.info("Event does not trigger anything {}", eventName);
    }
  }
}
