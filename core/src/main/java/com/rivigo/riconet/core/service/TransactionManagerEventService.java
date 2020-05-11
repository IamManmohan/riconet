package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.dto.NotificationDTO;
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
      CollectionRequestDto collectionRequestDto =
          objectMapper.convertValue(
              notificationDTO.getMetadata().get(COLLECTIONS_PAYLOAD), CollectionRequestDto.class);
      transactionManagerService.hitTransactionManagerAndLogResponse(collectionRequestDto);
    } else {
      log.error("Collections payload doesn't exist in notification: {}", notificationDTO);
    }
  }
}
