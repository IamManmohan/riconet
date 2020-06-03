package com.rivigo.riconet.event.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import com.rivigo.riconet.core.service.TransactionManagerService;
import java.io.IOException;
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
  private static final String COLLECTION_PAYLOAD = "collectionPayload";

  /**
   * This function processes notification for transaction manager.
   *
   * @param notificationDTO dto for notification.
   */
  public void processNotification(NotificationDTO notificationDTO) throws IOException {
    if (!notificationDTO.getMetadata().containsKey(COLLECTION_PAYLOAD)) {
      log.error("Collections payload doesn't exist in notification: {}", notificationDTO);
    }
    if (TransactionManagerEventNames.CN_INVALIDATION_COLLECTIONS
        .name()
        .equals(notificationDTO.getEventName())) {
      log.info("Cn Invalidation Event");
      transactionManagerService.rollbackTransactionsAndLogResponse(
          notificationDTO.getMetadata().get(COLLECTION_PAYLOAD));
    } else {
      log.info("Collections Event");
      transactionManagerService.hitTransactionManagerAndLogResponse(
          notificationDTO.getMetadata().get(COLLECTION_PAYLOAD));
    }
  }
}
