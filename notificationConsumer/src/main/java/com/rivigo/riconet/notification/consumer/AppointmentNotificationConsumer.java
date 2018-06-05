package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.ConsignmentAppointmentService;
import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentNotificationConsumer extends ConsumerModel {

  @Autowired private ConsignmentAppointmentService consignmentAppointmentService;

  private ObjectMapper objectMapper;

  public AppointmentNotificationConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_APPOINTMENT_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_APPOINTMENT_NOTIFICATION_ERROR.name();
  }

  @Autowired private TopicNameConfig topicNameConfig;

  public void processMessage(String str) throws IOException {
    TypeReference<AppointmentNotificationDTO> mapType =
        new TypeReference<AppointmentNotificationDTO>() {};
    AppointmentNotificationDTO appointmentNotificationDTO = objectMapper.readValue(str, mapType);
    consignmentAppointmentService.processAppointmentNotification(appointmentNotificationDTO);
  }
}
