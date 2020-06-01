package com.rivigo.riconet.event.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import com.rivigo.riconet.core.service.TransactionManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service for dealing with notification DTOs relating to transaction manager. */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerEventService {

  /** bean of transaction manager service for hitting transaction manager APIs. */
  private final TransactionManagerService transactionManagerService;

  /** key constant for collections payload. */
  private static final String collectionsPayload = "collectionPayload";

  /**
   * This function processes notification for transaction manager.
   *
   * @param notificationDTO dto for notification.
   */
  public void processNotification(NotificationDTO notificationDTO) {
    if (!notificationDTO.getMetadata().containsKey(collectionsPayload)) {
      log.error("Collections payload doesn't exist in notification: {}", notificationDTO);
    }
    if (TransactionManagerEventNames.CN_INVALIDATION_COLLECTIONS
        .name()
        .equals(notificationDTO.getEventName())) {
      transactionManagerService.rollbackTransactionsAndLogResponse(
          notificationDTO.getMetadata().get(collectionsPayload));
    } else {
      transactionManagerService.hitTransactionManagerAndLogResponse(
          notificationDTO.getMetadata().get(collectionsPayload));
    }
  }
}
