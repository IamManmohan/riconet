package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.PickupService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BfPickupChargesActionConsumer extends EventConsumer {

  private ObjectMapper objectMapper;

  @Autowired
  private PickupService pickupService;

  @Autowired
  private TopicNameConfig topicNameConfig;

  public BfPickupChargesActionConsumer() {
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
  public List<EventName> eventNamesToBeConsumed(){
    return Arrays.asList(EventName.CN_COMPLETION_ALL_INSTANCES,EventName.CN_DELETED,EventName.PICKUP_COMPLETION);
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    pickupService.deductPickupCharges(notificationDTO);
  }
}
