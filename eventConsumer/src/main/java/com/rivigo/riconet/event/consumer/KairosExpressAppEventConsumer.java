package com.rivigo.riconet.event.consumer;

import static com.rivigo.riconet.core.constants.EventConsumerNameConstants.KAIROS_EXPRESS_APP_EVENT_CONSUMER;

import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.KairosExpressAppEventName;
import com.rivigo.riconet.core.service.KairosExpressAppEventTriggerService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KairosExpressAppEventConsumer extends EventConsumer {

  @Autowired private TopicNameConfig topicNameConfig;

  @Autowired private KairosExpressAppEventTriggerService kairosExpressAppEventTriggerService;

  @Override
  public String getTopic() {
    return topicNameConfig.kairosExpressAppSinkTopic();
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(KairosExpressAppEventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    kairosExpressAppEventTriggerService.processNotification(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return KAIROS_EXPRESS_APP_EVENT_CONSUMER;
  }
}
