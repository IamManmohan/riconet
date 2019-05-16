package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.ExpressAppPickupService;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpressAppPickupConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private TopicNameConfig topicNameConfig;

  @Autowired private ExpressAppPickupService expressAppPickupService;

  private ExpressAppPickupConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return topicNameConfig.expressAppPickupSink();
  }

  @Override
  public String getErrorTopic() {
    return topicNameConfig.expressAppPickupSinkError();
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Express App Pickup Consumer {}", str);
    Map<String, String> map;
    try {
      map = objectMapper.readValue(str, Map.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return;
    }
    log.debug("Event Payload {}", map);
    expressAppPickupService.processExpressPickupEvent(map);
  }
}
