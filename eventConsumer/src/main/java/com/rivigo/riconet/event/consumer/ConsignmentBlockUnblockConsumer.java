package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by imran on 24/5/18.
 */
@Slf4j
@Component
public class ConsignmentBlockUnblockConsumer extends EventConsumer {

  private final ObjectMapper objectMapper;

  private final ConsignmentBlockUnblockService consignmentBlockUnblockService;

  private final TopicNameConfig topicNameConfig;

  @Autowired
  public ConsignmentBlockUnblockConsumer(
      ObjectMapper objectMapper,
      ConsignmentBlockUnblockService consignmentBlockUnblockService,
      TopicNameConfig topicNameConfig) {
    this.objectMapper = objectMapper;
    this.consignmentBlockUnblockService = consignmentBlockUnblockService;
    this.topicNameConfig = topicNameConfig;
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public List<EventName> eventNamesToBeConsumed() {
    return Arrays.asList(EventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    consignmentBlockUnblockService.processNotification(notificationDTO);
  }

}
