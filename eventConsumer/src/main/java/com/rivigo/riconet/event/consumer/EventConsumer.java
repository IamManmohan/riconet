package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class EventConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private TopicNameConfig topicNameConfig;

  public abstract List<Enum> eventNamesToBeConsumed();

  public abstract void doAction(NotificationDTO notificationDTO);

  public abstract String getConsumerName();

  public EventConsumer() {
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
  public void processMessage(String str) {
    log.info("Processing message in {} {}", getConsumerName(), str);
    NotificationDTO notificationDTO;
    try {
      notificationDTO = objectMapper.readValue(str, NotificationDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return;
    }
    log.debug("NotificationDTO {}", notificationDTO);
    if (eventNamesToBeConsumed()
        .stream()
        .map(Enum::name)
        .collect(Collectors.toSet())
        .contains(notificationDTO.getEventName())) {
      doAction(notificationDTO);
    } else {
      log.debug(
          "NotificationDTO is not consumed by {} as eventName {} ",
          getConsumerName(),
          notificationDTO.getEventName());
    }
  }
}
