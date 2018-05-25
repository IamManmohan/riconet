package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.EventTriggerService;
import com.rivigo.riconet.core.service.PickupService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsignmentCompletionPickupChargesActionConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired
  private PickupService pickupService;

  @Autowired
  private TopicNameConfig topicNameConfig;

  public ConsignmentCompletionPickupChargesActionConsumer() {
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
  public String processMessage(String str) {
    log.info("Processing message in ZoomEventTrigger {}", str);
    NotificationDTO notificationDTO = null;
    try {
      notificationDTO = objectMapper.readValue(str, NotificationDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return str;
    }
    log.debug("NotificationDTO {}", notificationDTO);
    if(EventName.CN_COMPLETION_ALL_INSTANCES.equals(notificationDTO.getEventName())){
      pickupService.deductPickupCharges(notificationDTO.getMetadata());
    }
    return str;
  }
}
