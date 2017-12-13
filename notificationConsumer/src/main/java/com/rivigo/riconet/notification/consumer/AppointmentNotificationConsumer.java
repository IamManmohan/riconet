package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.ConsignmentAppointmentService;
import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AppointmentNotificationConsumer extends ConsumerModel {

  @Autowired
  ConsignmentAppointmentService consignmentAppointmentService;

  ObjectMapper objectMapper ;

  @Value("${zoom.mysql.url}")
  private String mysqlURL;

  public String processMessage(String str) throws IOException {
    TypeReference<AppointmentNotificationDTO> mapType = new TypeReference<AppointmentNotificationDTO>() {};
    log.info(str);
    log.info(mysqlURL);
    AppointmentNotificationDTO appointmentNotificationDTO = objectMapper.readValue(str, mapType);
    consignmentAppointmentService.processAppointmentNotification(appointmentNotificationDTO);
    return str;
  }

  public AppointmentNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_APPOINTMENT_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_APPOINTMENT_NOTIFICATION_ERROR.name(),5L);
    objectMapper=new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
