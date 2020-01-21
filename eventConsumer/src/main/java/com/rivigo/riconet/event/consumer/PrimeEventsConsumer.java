package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;
import com.rivigo.riconet.core.service.PrimeEventService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrimeEventsConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private PrimeEventService primeEventService;

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  public PrimeEventsConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.getPrimeEventSink();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.getPrimeEventSinkError();
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Prime Events Consumer {}", str);
    PrimeEventDto primeEventDto;
    try {
      primeEventDto = objectMapper.readValue(str, PrimeEventDto.class);
    } catch (IOException ex) {
      log.error("Error occurred while processing message {} ", str, ex);
      return;
    }
    primeEventService.processEvent(primeEventDto);
  }
}
