package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.WmsEventName;
import com.rivigo.riconet.core.service.WmsEventTriggerService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WmsEventConsumer extends EventConsumer {

  @Autowired private WmsEventTriggerService wmsEventTriggerService;

  @Autowired private TopicNameConfig topicNameConfig;

  @Override
  public String getTopic() {
    return topicNameConfig.wmsEventSink();
  }

  @Override
  public String getErrorTopic() {
    return topicNameConfig.wmsEventSinkError();
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(WmsEventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    wmsEventTriggerService.processNotification(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "WmsEventConsumer";
  }
}
