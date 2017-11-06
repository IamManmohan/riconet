package com.rivigo.riconet.notification.consumer;

import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.DEPSType;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rivigo.riconet.core.service.DEPSRecordService;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DEPSNotificationConsumer extends ConsumerModel {


  @Autowired
  private DEPSRecordService depsRecordService;

  ObjectMapper objectMapper ;

  public String processMessage(String str){
    DEPSNotificationContext context = null;
    try {
      TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {};
      List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(str, mapType);
      Set<Long> shortageConsignmentIds = depsRecordList.stream()
              .filter(depsRecord -> depsRecord.getDepsType()== DEPSType.SHORTAGE)
              .map(DEPSNotificationDTO::getConsignmentId).collect(Collectors.toSet());

      if(CollectionUtils.isEmpty(shortageConsignmentIds)) {
        log.info("No shortage tickets found ");
      }else {
        context = depsRecordService.getNotificationContext(depsRecordList);
        List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
        depsRecordService.sendNotifications(depsNotificationList);
      }
    }catch (Exception e){
      log.error("DepsNotification mapping failed", e);
        throw  new ZoomException("Error in message format");
    }
    return str;
  }

  public DEPSNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name(),5L);
    objectMapper=new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
