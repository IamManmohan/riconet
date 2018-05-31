package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.EventTriggerService;
import de.flapdoodle.embed.process.collections.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 19/4/18. */
@Slf4j
@Component
public class ZoomEventTriggerConsumer extends EventConsumer {

  private ObjectMapper objectMapper;

  @Autowired private EventTriggerService eventTriggerService;

  @Autowired private TopicNameConfig topicNameConfig;

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
  public List<EventName> eventNamesToBeConsumed() {
    return Collections.newArrayList(EventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    eventTriggerService.processNotification(notificationDTO);
  }
}
