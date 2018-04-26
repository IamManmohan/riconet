package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketEntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ashfakh on 19/4/18.
 */
@Slf4j
@Service

public class EventTriggerService {

  private static final String CNOTE = "CNOTE";

  @Autowired
  TicketingClientService ticketingClientService;

  public void processNotification(NotificationDTO notificationDTO) {
    EventName eventName = notificationDTO.getEventName();
    switch (eventName) {
      case CN_DELIVERY:
        String entityId = notificationDTO.getMetadata().get(CNOTE);
        ticketingClientService
            .closeTicket(entityId, TicketEntityType.CN.name(), EventName.CN_DELIVERY);
      case PICKUP_COMPLETION:
        ticketingClientService
            .closeTicket(notificationDTO.getEntityId().toString(), TicketEntityType.PRQ.name(),
                EventName.PICKUP_COMPLETION);
      case DEFAULT:
        log.info("Event does not trigger anything {}", eventName);
    }
  }
}
