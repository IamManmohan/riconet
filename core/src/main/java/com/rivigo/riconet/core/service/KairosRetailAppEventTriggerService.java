package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.KairosRetailAppEventName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KairosRetailAppEventTriggerService {

  @Autowired AppNotificationService appNotificationService;

  public void processNotification(NotificationDTO notificationDTO) {
    KairosRetailAppEventName eventName =
        KairosRetailAppEventName.valueOf(notificationDTO.getEventName());
    switch (eventName) {
      case CN_DELAYED:
        appNotificationService.sendCnDelayedNotification(notificationDTO);
        break;
      default:
        log.info("Event does not trigger anything {}", eventName);
    }
  }
}
