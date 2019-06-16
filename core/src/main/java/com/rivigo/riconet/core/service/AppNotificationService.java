package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.WmsEventName;

/** Created by ashfakh on 21/09/18. */
public interface AppNotificationService {

  void sendTaskNotifications(NotificationDTO notificationDTO, WmsEventName eventName);

  void sendShopFloorStatusUpdateNotifications(NotificationDTO notificationDTO);

  void sendCnFirstOuDispatchNotification(NotificationDTO notificationDTO);

  void sendCnDrsDispatchEvent(NotificationDTO notificationDTO);

  void sendCnDeliveredNotification(NotificationDTO notificationDTO);

  void sendCnDelayedNotification(NotificationDTO notificationDTO);

  // Express app notification that was introduced in V1 but not being used in its revamp.
  void sendPickUpAssignmentEvent(NotificationDTO notificationDTO);

  // Express app notification that was introduced in V1 but not being used in its revamp.
  void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO);

  // Express app notification that was introduced in V1 but not being used in its revamp.
  void sendPickupCancellationNotification(NotificationDTO notificationDTO);
}
