package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerEventService {

  private final TransactionManagerService transactionManagerService;

  private final ObjectMapper objectMapper;

  private static String COLLECTIONS_PAYLOAD = "collectionPayload";

  public void processNotification(NotificationDTO notificationDTO) {
    if (notificationDTO.getMetadata().containsKey(COLLECTIONS_PAYLOAD)) {
      if (TransactionManagerEventNames.CN_INVALIDATION_COLLECTIONS
          .name()
          .equals(notificationDTO.getEventName())) {
        transactionManagerService.rollbackTransactionsAndLogResponse(
            notificationDTO.getMetadata().get(COLLECTIONS_PAYLOAD));
      } else {
        transactionManagerService.hitTransactionManagerAndLogResponse(
            notificationDTO.getMetadata().get(COLLECTIONS_PAYLOAD));
      }
    } else {
      log.error("Collections payload doesn't exist in notification: {}", notificationDTO);
    }
  }
}
