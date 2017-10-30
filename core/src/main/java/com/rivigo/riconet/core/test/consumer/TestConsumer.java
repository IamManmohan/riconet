package com.rivigo.riconet.core.test.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TestConsumer extends ConsumerModel {


  @Autowired
  private DEPSRecordService depsRecordService;

  public String processMessage(String str){
    if(str.equals("1")){
      return str;
    }
    throw  new ZoomException("test");
  }

  public TestConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name());
  }
}
