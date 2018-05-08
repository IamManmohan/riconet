package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/**
 * Created by ashfakh on 8/5/18.
 */
public interface ChequeBounceService {

  void consumeChequeBounceEvent(NotificationDTO notificationDTO);

}
