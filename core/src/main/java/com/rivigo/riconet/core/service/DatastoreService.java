package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface DatastoreService {

  void cleanupAddressesUsingEwaybillMetadata(NotificationDTO notificationDTO);
}
