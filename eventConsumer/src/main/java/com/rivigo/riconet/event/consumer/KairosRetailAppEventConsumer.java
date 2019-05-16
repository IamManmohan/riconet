package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.KairosRetailAppEventName;
import com.rivigo.riconet.core.service.KairosRetailAppEventTriggerService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KairosRetailAppEventConsumer extends EventConsumer {

  @Autowired private TopicNameConfig topicNameConfig;

  @Autowired private KairosRetailAppEventTriggerService kairosRetailAppEventTriggerService;

  @Override
  public String getTopic() {
    return topicNameConfig.kairosRetailAppSinkTopic();
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(KairosRetailAppEventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    kairosRetailAppEventTriggerService.processNotification(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "KairosRetailAppEventConsumer";
  }
}
