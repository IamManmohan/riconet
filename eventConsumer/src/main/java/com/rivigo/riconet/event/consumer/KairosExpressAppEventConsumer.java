package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.KairosExpressAppEventName;
import com.rivigo.riconet.core.service.KairosExpressAppEventTriggerService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KairosExpressAppEventConsumer extends EventConsumer {

  @Autowired private EventTopicNameConfig eventTopicNameConfig;

  @Autowired private KairosExpressAppEventTriggerService kairosExpressAppEventTriggerService;

  @Override
  public String getTopic() {
    return eventTopicNameConfig.kairosExpressAppSinkTopic();
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
    return this.getClass().getName();
  }
}
