package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.athenagps.AthenaGpsEventDto;
import com.rivigo.riconet.core.service.AthenaGpsEventService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AthenaGpsEventsConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private AthenaGpsEventService athenaGpsEventService;

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  public AthenaGpsEventsConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.getAthenaGpsEventSink();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.getAthenaGpsEventSinkError();
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Athena GPS Events Consumer {}", str);
    AthenaGpsEventDto athenaGpsEventDto;
    try {
      athenaGpsEventDto = objectMapper.readValue(str, AthenaGpsEventDto.class);
    } catch (IOException ex) {
      log.error("Error occurred while processing message {} ", str, ex);
      return;
    }
    athenaGpsEventService.processEvent(athenaGpsEventDto);
  }
}
