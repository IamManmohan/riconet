package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.service.EventTriggerService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ashfakh on 19/4/18.
 */

@Slf4j
@Component
public class ZoomEventTriggerConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired
  private EventTriggerService eventTriggerService;

  @Autowired
  private TopicNameConfig topicNameConfig;

  public ZoomEventTriggerConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return topicNameConfig.enrichedEventSinkTopic();
  }

  @Override
  public String getErrorTopic() {
    return topicNameConfig.enrichedEventSinkErrorTopic();
  }

  @Override
  public String processMessage(String str) throws IOException {
    log.info("Processing message in ZoomEventTrigger {}", str);
    NotificationDTO notificationDTO = null;
    try {
      notificationDTO = objectMapper.readValue(str, NotificationDTO.class);
      log.debug("NotificationDTO {}", notificationDTO);
      eventTriggerService.processNotification(notificationDTO);
    } catch (Exception e) {
      log.error("failed", e);
    }
    return str;
  }
}
