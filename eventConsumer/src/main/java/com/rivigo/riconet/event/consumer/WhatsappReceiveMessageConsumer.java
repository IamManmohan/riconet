package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.whatsappbot.BasePubSubDto;
import com.rivigo.riconet.core.dto.whatsappbot.ReceiveWhatsappMessageDto;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import com.rivigo.riconet.event.service.WhatsappBotService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WhatsappReceiveMessageConsumer extends ConsumerModel {

  private final EventTopicNameConfig eventTopicNameConfig;

  private final WhatsappBotService whatsappBotService;

  private final ObjectMapper objectMapper;

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
