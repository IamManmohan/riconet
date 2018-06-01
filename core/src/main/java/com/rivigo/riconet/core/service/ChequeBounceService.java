package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;

/** Created by ashfakh on 8/5/18. */
public interface ChequeBounceService {

  TicketDTO consumeChequeBounceEvent(NotificationDTO notificationDTO);
}
