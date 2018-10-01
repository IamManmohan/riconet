package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/** Created by ashfakh on 21/09/18. */
public interface AppNotificationService {

  void sendUnloadingInLoadingNotification(NotificationDTO notificationDTO);

  void sendLoadingUnloadingNotification(NotificationDTO notificationDTO);
}
