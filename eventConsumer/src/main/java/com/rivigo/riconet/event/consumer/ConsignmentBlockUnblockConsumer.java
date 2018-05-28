package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.controller.Response;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by imran on 24/5/18.
 */

@Slf4j
@Component
public class ConsignmentBlockUnblockConsumer extends ConsumerModel {

  private final ObjectMapper objectMapper;

  private final ConsignmentBlockUnblockService consignmentBlockUnblockService;

  private final TopicNameConfig topicNameConfig;

  @Autowired
  public ConsignmentBlockUnblockConsumer(ObjectMapper objectMapper, ConsignmentBlockUnblockService consignmentBlockUnblockService, TopicNameConfig topicNameConfig) {
    this.objectMapper = objectMapper;
    this.consignmentBlockUnblockService = consignmentBlockUnblockService;
    this.topicNameConfig = topicNameConfig;
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
    log.info("Processing message in {} {}", this.getClass().getName(), str);
    NotificationDTO notificationDTO = null;
    try {
      notificationDTO = objectMapper.readValue(str, NotificationDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return Response.RequestStatus.FAILURE.name() ;
    }
    log.debug("NotificationDTO {}", notificationDTO);
    consignmentBlockUnblockService.processNotification(notificationDTO);
    return Response.RequestStatus.SUCCESS.name();
  }
}
