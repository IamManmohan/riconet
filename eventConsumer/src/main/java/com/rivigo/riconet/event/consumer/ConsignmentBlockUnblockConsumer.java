package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.CnBlockUnblockEventName;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by imran on 24/5/18. */
@Slf4j
@Component
public class ConsignmentBlockUnblockConsumer extends EventConsumer {

  private final ObjectMapper objectMapper;

  private final ConsignmentBlockUnblockService consignmentBlockUnblockService;

  @Autowired
  public ConsignmentBlockUnblockConsumer(
      ObjectMapper objectMapper, ConsignmentBlockUnblockService consignmentBlockUnblockService) {
    this.objectMapper = objectMapper;
    this.consignmentBlockUnblockService = consignmentBlockUnblockService;
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(CnBlockUnblockEventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    consignmentBlockUnblockService.processNotification(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "ConsignmentBlockUnblockConsumer";
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.consignmentBlockUnblockError();
  }
}
