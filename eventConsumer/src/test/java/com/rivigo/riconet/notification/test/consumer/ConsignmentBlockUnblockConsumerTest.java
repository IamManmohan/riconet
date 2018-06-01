package com.rivigo.riconet.notification.test.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.controller.Response;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
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

  private TopicNameConfig topicNameConfig = Mockito.mock(TopicNameConfig.class);

  @InjectMocks
  private ConsignmentBlockUnblockService consignmentBlockUnblockService =
      new ConsignmentBlockUnblockServiceImpl(apiClientService);

  @InjectMocks
  private ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer =
      new ConsignmentBlockUnblockConsumer(
          objectMapper, consignmentBlockUnblockService, topicNameConfig);

  private static final Long ENTITY_ID = 1L;

  private static final Long REASON_ID = 1L;

  @Test
  public void getTopicTest() {
    String enrichedEventSink = "enriche-event-sink";
    Mockito.when(topicNameConfig.enrichedEventSinkTopic()).thenReturn(enrichedEventSink);
    assert enrichedEventSink.equalsIgnoreCase(consignmentBlockUnblockConsumer.getTopic());
  }

  @Test
  public void getErrorTopicTest() {
    String enrichedEventSinkEroor = "enriche-event-sink-error";
    Mockito.when(topicNameConfig.enrichedEventSinkTopic()).thenReturn(enrichedEventSinkEroor);
    assert enrichedEventSinkEroor.equalsIgnoreCase(consignmentBlockUnblockConsumer.getTopic());
  }

  @Test
  public void processMessageCod() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.COD.name());
    NotificationDTO dto =
        NotificationDTO.builder()
            .eventName(EventName.COLLECTION_CHEQUE_BOUNCE)
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    assert consignmentBlockUnblockConsumer
        .processMessage(message)
        .equalsIgnoreCase(Response.RequestStatus.SUCCESS.toString());
  }

  @Test
  public void processMessagePrepaid() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.PREPAID.name());
    metadata.put(ZoomCommunicationFieldNames.Reason.REASON.name(), REASON_ID.toString());
    NotificationDTO dto =
        NotificationDTO.builder()
            .entityId(ENTITY_ID)
            .eventName(EventName.COLLECTION_CHEQUE_BOUNCE)
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    assert consignmentBlockUnblockConsumer
        .processMessage(message)
        .equalsIgnoreCase(Response.RequestStatus.SUCCESS.toString());
  }

  @Test
  public void processMessageUnblock() throws JsonProcessingException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PAYMENT_MODE.name(), PaymentMode.PREPAID.name());
    metadata.put(ZoomCommunicationFieldNames.Reason.REASON.name(), REASON_ID.toString());
    NotificationDTO dto =
        NotificationDTO.builder()
            .entityId(ENTITY_ID)
            .eventName(EventName.CN_COLLECTION_CHEQUE_BOUNCE_TICKET_CLOSED)
            .metadata(metadata)
            .build();
    String message = objectMapper.writeValueAsString(dto);
    assert consignmentBlockUnblockConsumer
        .processMessage(message)
        .equalsIgnoreCase(Response.RequestStatus.SUCCESS.toString());
  }
}
