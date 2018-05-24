package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.ConsignmentAppointmentService;
import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.enums.AppointmentNotificationType;
import com.rivigo.zoom.common.model.ConsignmentAppointmentRecord;
import com.rivigo.zoom.common.repository.mysql.ConsignmentAppointmentRepository;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ConsignmentAppointmentServiceTest {

  @Autowired ConsignmentAppointmentService consignmentAppointmentService;

  @Autowired ConsignmentAppointmentRepository consignmentAppointmentRepository;

  public String processMessage(String str) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    DEPSNotificationContext context = null;
    TypeReference<AppointmentNotificationDTO> mapType =
        new TypeReference<AppointmentNotificationDTO>() {};
    AppointmentNotificationDTO appointmentNotificationDTO = objectMapper.readValue(str, mapType);
    consignmentAppointmentService.processAppointmentNotification(appointmentNotificationDTO);
    return str;
  }

  public void appointmentMissedSummaryNotification() throws IOException {
    ConsignmentAppointmentRecord appointment =
        consignmentAppointmentRepository.findByConsignmentIdAndIsActive(9l, true);
    appointment.setAppointmentTime(DateTime.now().minusDays(1));
    consignmentAppointmentRepository.save(appointment);
    String str = "{\"notificationType\":\"APPOINTMENT_MISSED_SUMMARY\"}";
    processMessage(str);
  }

  public void appointmentNotOfdNotification() throws IOException {
    String str = "{\"notificationType\":\"APPOINTMENT_NOT_OFD_FIRST_HALF\"}";
    processMessage(str);
  }

  public void appointmentMissedNotification() throws IOException {
    ConsignmentAppointmentRecord appointment =
        consignmentAppointmentRepository.findByConsignmentIdAndIsActive(9l, true);
    appointment.setAppointmentTime(DateTime.now().minusMinutes(30));
    consignmentAppointmentRepository.save(appointment);
    String time = Long.toString(DateTime.now().minusMinutes(60).getMillis());
    String str = "{\"notificationType\":\"APPOINTMENT_MISSED\",\"lastExecutionTime\":" + time + "}";
    processMessage(str);
  }

  public void appointmentDeliveredLateNotification() throws IOException {
    AppointmentNotificationDTO dto = new AppointmentNotificationDTO();
    dto.setConsignmentId(11l);
    dto.setNotificationType(AppointmentNotificationType.APPOINTMENT_DELIVERED_LATE);
    dto.setDeliveryTime(DateTime.now().getMillis());
    dto.setAppoitnmentTime(DateTime.now().minusMinutes(30).getMillis());
    ObjectMapper obj = new ObjectMapper();
    String str = obj.writeValueAsString(dto);
    processMessage(str);
  }

  public void appointmentDeliveredDayLateNotification() throws IOException {
    AppointmentNotificationDTO dto = new AppointmentNotificationDTO();
    dto.setConsignmentId(11l);
    dto.setNotificationType(AppointmentNotificationType.APPOINTMENT_DELIVERED_LATE);
    dto.setDeliveryTime(DateTime.now().getMillis());
    dto.setAppoitnmentTime(DateTime.now().minusDays(2).getMillis());
    ObjectMapper obj = new ObjectMapper();
    String str = obj.writeValueAsString(dto);
    processMessage(str);
  }
}
