package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.whatsappbot.BasePubSubDto;
import com.rivigo.riconet.core.dto.whatsappbot.ReceiveWhatsappMessageDto;
import com.rivigo.riconet.core.service.WhatsappBotService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WhatsappReceiveMessageConsumer extends ConsumerModel {

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  @Autowired private WhatsappBotService whatsappBotService;

  private ObjectMapper objectMapper;

  public WhatsappReceiveMessageConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.getWhatsappMessageReceive();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.getWhatsappMessageReceiveError();
  }

  public void processMessage(String str) throws IOException {
    BasePubSubDto basePubSubDto = objectMapper.readValue(str, BasePubSubDto.class);
    ReceiveWhatsappMessageDto receiveWhatsappMessageDto =
        objectMapper.readValue(basePubSubDto.getMessage(), ReceiveWhatsappMessageDto.class);
    whatsappBotService.processReceivedWhatsappMessage(receiveWhatsappMessageDto);
  }
}
