package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.ZoomCommunicationsSMSDTO;
import com.rivigo.riconet.core.service.ZoomCommunicationsService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by aditya on 22/2/18. */
@Slf4j
@Component
public class ZoomCommunicationsConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  @Autowired private ZoomCommunicationsService zoomCommunicationsService;

  @Autowired private TopicNameConfig topicNameConfig;

  public ZoomCommunicationsConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    log.info(" Zoom Communication Consumer topic {}", topicNameConfig.smsSink());
    return topicNameConfig.smsSink();
  }

  @Override
  public String getErrorTopic() {
    log.info(" Zoom Communication Consumer error topic {}", topicNameConfig.smsSinkError());
    return topicNameConfig.smsSinkError();
  }

  public void processMessage(String str) throws IOException {
    log.info("Processing message in ZoomCommunicationConsumer {}", str);
    ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO = null;
    try {
      zoomCommunicationsSMSDTO = objectMapper.readValue(str, ZoomCommunicationsSMSDTO.class);
      log.debug("ZoomCommunicationsSMSDTO {}", zoomCommunicationsSMSDTO);
    } catch (Exception e) {
      log.error("failed", e);
    }
    zoomCommunicationsService.processNotificationMessage(zoomCommunicationsSMSDTO);
  }
}
