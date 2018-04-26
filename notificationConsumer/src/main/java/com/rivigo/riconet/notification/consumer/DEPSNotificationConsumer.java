package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.DEPSType;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DEPSNotificationConsumer extends ConsumerModel {


  private ObjectMapper objectMapper;

  @Autowired
  private DEPSRecordService depsRecordService;

  public DEPSNotificationConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name();
  }

  public String processMessage(String str) throws IOException {
    DEPSNotificationContext context = null;
    TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {
    };
    List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(str, mapType);
    Set<Long> shortageConsignmentIds = depsRecordList.stream()
        .filter(depsRecord -> depsRecord.getDepsType() == DEPSType.SHORTAGE)
        .map(DEPSNotificationDTO::getConsignmentId).collect(Collectors.toSet());

    if (CollectionUtils.isEmpty(shortageConsignmentIds)) {
      log.info("No shortage tickets found ");
    } else {
      context = depsRecordService.getNotificationContext(depsRecordList);
      List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
      depsRecordService.sendNotifications(depsNotificationList);
    }
    return str;
  }
}
