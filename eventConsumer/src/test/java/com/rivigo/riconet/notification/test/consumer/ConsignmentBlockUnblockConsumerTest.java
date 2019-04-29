package com.rivigo.riconet.notification.test.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.CnBlockUnblockEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.event.consumer.ConsignmentBlockUnblockConsumer;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import com.rivigo.riconet.event.service.impl.ConsignmentBlockUnblockServiceImpl;
import com.rivigo.zoom.common.enums.PaymentMode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

public class ConsignmentBlockUnblockConsumerTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  private ApiClientService apiClientService = Mockito.mock(ApiClientService.class);

  @InjectMocks
  private ConsignmentBlockUnblockService consignmentBlockUnblockService =
      new ConsignmentBlockUnblockServiceImpl(apiClientService);

  @InjectMocks
  private ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer =
      new ConsignmentBlockUnblockConsumer(objectMapper, consignmentBlockUnblockService);

  private static final Long ENTITY_ID = 1L;

  private static final Long REASON_ID = 1L;

  @Test
  public void processMessageCod() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.TO_PAY.name());
    NotificationDTO dto =
        NotificationDTO.builder()
            .eventName(CnBlockUnblockEventName.COLLECTION_CHEQUE_BOUNCE.name())
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    consignmentBlockUnblockConsumer.processMessage(message);
  }

  @Test
  public void processMessagePaid() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.PAID.name());
    metadata.put(ZoomCommunicationFieldNames.Reason.REASON.name(), REASON_ID.toString());
    NotificationDTO dto =
        NotificationDTO.builder()
            .entityId(ENTITY_ID)
            .eventName(CnBlockUnblockEventName.COLLECTION_CHEQUE_BOUNCE.name())
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    consignmentBlockUnblockConsumer.processMessage(message);
  }

  @Test
  public void processMessageUnblock() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.PAID.name());
    metadata.put(ZoomCommunicationFieldNames.Reason.REASON.name(), REASON_ID.toString());
    NotificationDTO dto =
        NotificationDTO.builder()
            .entityId(ENTITY_ID)
            .eventName(CnBlockUnblockEventName.CN_COLLECTION_CHEQUE_BOUNCE_TICKET_CLOSED.name())
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    consignmentBlockUnblockConsumer.processMessage(message);
  }
}
