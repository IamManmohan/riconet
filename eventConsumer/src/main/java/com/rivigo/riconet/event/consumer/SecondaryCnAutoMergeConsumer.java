package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.CnBlockUnblockEventName;
import com.rivigo.riconet.event.service.ConsignmentAutoMergeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.rivigo.riconet.core.enums.SecondaryCnAutoMergeEventName.SECONDARY_CN_AUTO_MERGE;

@Slf4j
@Component
public class SecondaryCnAutoMergeConsumer extends EventConsumer {

  private final ObjectMapper objectMapper;

  private final ConsignmentAutoMergeService consignmentAutoMergeService;

  @Autowired
  public SecondaryCnAutoMergeConsumer(
      ObjectMapper objectMapper, ConsignmentAutoMergeService consignmentAutoMergeService) {
    this.objectMapper = objectMapper;
    this.consignmentAutoMergeService = consignmentAutoMergeService;
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Collections.singletonList(SECONDARY_CN_AUTO_MERGE);
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    consignmentAutoMergeService.autoMergeSecondaryConsignment(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "SecondaryCnAutoMergeConsumer";
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.secondaryCnAutoMergeError();
  }
}
