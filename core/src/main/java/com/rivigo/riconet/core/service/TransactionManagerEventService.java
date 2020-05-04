package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionManagerEventService {

  public void processNotification(NotificationDTO notificationDTO) {
    TransactionManagerEventNames eventName =
        TransactionManagerEventNames.valueOf(notificationDTO.getEventName());
    switch (eventName) {
      case CN_EDIT_COLLECTIONS:
      case CN_PAYMENT_COLLECTIONS:
      case CN_CREATION_COLLECTIONS:
      case CHEQUE_BOUNCE_BANK_TRANSFER:
      case NORMAL_TO_NORMAL_TO_PAY_CHANGE:
      case PAYMENT_CHANGE_TO_BANK_TRANSFER:
      case CN_HANDOVER_COMPLETED_COLLECTIONS:
      default:
        log.info("Event does not trigger anything {}", eventName);
        break;
    }
  }
}
