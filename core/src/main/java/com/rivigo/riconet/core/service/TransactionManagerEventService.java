package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.dto.NotificationDTO;
import java.io.IOException;
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

  String COLLECTIONS_PAYLOAD = "collectionPayload";

  public void processNotification(NotificationDTO notificationDTO) {
    if (notificationDTO.getMetadata().containsKey(COLLECTIONS_PAYLOAD)) {
      CollectionRequestDto collectionRequestDto = null;
      try {
        collectionRequestDto =
            objectMapper.readValue(
                notificationDTO.getMetadata().get(COLLECTIONS_PAYLOAD), CollectionRequestDto.class);
        transactionManagerService.hitTransactionManagerAndLogResponse(collectionRequestDto);
      } catch (IOException e) {
        log.error("Could not deserialize collections payload");
      }

    } else {
      log.error("Collections payload doesn't exist in notification: {}", notificationDTO);
    }
  }
}
