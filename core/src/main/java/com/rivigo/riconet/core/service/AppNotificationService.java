package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/** Created by ashfakh on 21/09/18. */
public interface AppNotificationService {

  void sendUnloadingInLoadingNotification(NotificationDTO notificationDTO);

  void sendLoadingUnloadingNotification(NotificationDTO notificationDTO);

  void sendPalletClosedNotification(NotificationDTO notificationDTO);

  void sendTaskClosedOrReassignedNotification(NotificationDTO notificationDTO);

  void sendIBClearEvent(NotificationDTO notificationDTO);

  void sendPickUpAssignmentEvent(NotificationDTO notificationDTO);

  void sendPickUpReachedAtClientAddress(NotificationDTO notificationDTO);

  void sendCnLoadedEvent(NotificationDTO notificationDTO);

  void sendCnOutForDelivery(NotificationDTO notificationDTO);

  void sendCnDeliveredNotification(NotificationDTO notificationDTO);
}
