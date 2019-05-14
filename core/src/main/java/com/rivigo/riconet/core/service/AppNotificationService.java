package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.WmsEventName;

/** Created by ashfakh on 21/09/18. */
public interface AppNotificationService {

  void sendTaskNotifications(NotificationDTO notificationDTO, WmsEventName eventName);

  void sendShopFloorStatusUpdateNotifications(NotificationDTO notificationDTO);

  void sendPickUpAssignmentEvent(NotificationDTO notificationDTO);

  void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO);

  void sendCnLoadedEvent(NotificationDTO notificationDTO);

  void sendCnDrsDispatchEvent(NotificationDTO notificationDTO);

  void sendCnDeliveredNotification(NotificationDTO notificationDTO);
}
