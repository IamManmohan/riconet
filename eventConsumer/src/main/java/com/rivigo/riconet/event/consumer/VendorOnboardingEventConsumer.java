package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class VendorOnboardingEventConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private FeederVendorService feederVendorService;

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  public VendorOnboardingEventConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.VendorOnboardingEventSink();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.VendorOnboardingEventSinkError();
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Compass Contract Events to Zoom Consumer {}", str);
    EventPayload eventPayload = null;
    try {
      eventPayload = objectMapper.readValue(str, EventPayload.class);
    } catch (IOException ex) {
      log.error("Error while processing message {} ", str, ex);
      return;
    }
    log.debug("Event Payload {}", eventPayload);
    feederVendorService.processVendorOnboardingEvent(eventPayload);
  }
}
