package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.FinanceEventService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 4/6/18. */
@Slf4j
@Component
public class FinanceEventsConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private FinanceEventService financeEventService;

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  public FinanceEventsConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.financeEventSink();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.financeEventSinkError();
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Finance Events Consumer {}", str);
    EventPayload eventPayload = null;
    try {
      eventPayload = objectMapper.readValue(str, EventPayload.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return;
    }
    log.debug("Event Payload {}", eventPayload);
    financeEventService.processFinanceEvents(eventPayload);
  }
}
