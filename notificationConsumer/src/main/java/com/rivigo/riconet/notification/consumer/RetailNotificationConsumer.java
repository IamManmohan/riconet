package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.RetailService;
import com.rivigo.zoom.common.dto.RetailNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class RetailNotificationConsumer extends ConsumerModel {

  @Autowired
  private RetailService retailService;

  private ObjectMapper objectMapper;

  public RetailNotificationConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_RETAIL_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_RETAIL_NOTIFICATION_ERROR.name();
  }

  @Autowired
  private TopicNameConfig topicNameConfig;


  public String processMessage(String str) throws IOException {
    List<RetailNotificationDTO> retailNotificationDTOList = null;
    TypeReference<List<RetailNotificationDTO>> mapType = new TypeReference<List<RetailNotificationDTO>>() {
    };
    retailNotificationDTOList = objectMapper.readValue(str, mapType);
    log.info("retail notification recieved {}", str);
    retailService.processRetailNotificationDTOList(retailNotificationDTOList);
    return str;
  }

}
