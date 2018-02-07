package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.ConsignmentAppointmentService;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.AppointmentNotificationType;
import com.rivigo.zoom.common.enums.PartnerType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.ConsignmentAppointmentRecord;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.repository.mysql.ConsignmentAppointmentRepository;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
public class ConsignmentAppointmentServiceTest extends TesterBase {

    @Autowired
    ConsignmentAppointmentService consignmentAppointmentService;

    @Autowired
    ConsignmentAppointmentRepository consignmentAppointmentRepository;

    public String processMessage(String str) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        DEPSNotificationContext context = null;
        TypeReference<AppointmentNotificationDTO> mapType = new TypeReference<AppointmentNotificationDTO>() {};
        AppointmentNotificationDTO appointmentNotificationDTO = objectMapper.readValue(str, mapType);
        consignmentAppointmentService.processAppointmentNotification(appointmentNotificationDTO);
        return str;
    }

    @Test
    public void appointmentMissedSummaryNotification() throws IOException {
        ConsignmentAppointmentRecord appointment=consignmentAppointmentRepository.findByConsignmentIdAndIsActive(9l,true);
        appointment.setAppointmentTime(DateTime.now().minusDays(1));
        consignmentAppointmentRepository.save(appointment);
        String str ="{\"notificationType\":\"APPOINTMENT_MISSED_SUMMARY\"}";
        processMessage(str);
    }

    @Test
    public void appointmentNotOfdNotification() throws IOException {
        String str ="{\"notificationType\":\"APPOINTMENT_NOT_OFD_FIRST_HALF\"}";
        processMessage(str);
    }

    @Test
    public void appointmentMissedNotification() throws IOException {
        ConsignmentAppointmentRecord appointment=consignmentAppointmentRepository.findByConsignmentIdAndIsActive(9l,true);
        appointment.setAppointmentTime(DateTime.now().minusMinutes(30));
        consignmentAppointmentRepository.save(appointment);
        String time= Long.toString( DateTime.now().minusMinutes(60).getMillis());
        String str ="{\"notificationType\":\"APPOINTMENT_MISSED\",\"lastExecutionTime\":"+time+"}";
        processMessage(str);
    }

    @Test
    public void appointmentDeliveredLateNotification() throws IOException {
        AppointmentNotificationDTO dto=new AppointmentNotificationDTO();
        dto.setConsignmentId(11l);
        dto.setNotificationType(AppointmentNotificationType.APPOINTMENT_DELIVERED_LATE);
        dto.setDeliveryTime(DateTime.now().getMillis());
        dto.setAppoitnmentTime(DateTime.now().minusMinutes(30).getMillis());
        ObjectMapper obj=new ObjectMapper();
        String str =obj.writeValueAsString(dto);
        processMessage(str);
    }

    @Test
    public void appointmentDeliveredDayLateNotification() throws IOException {
        AppointmentNotificationDTO dto=new AppointmentNotificationDTO();
        dto.setConsignmentId(11l);
        dto.setNotificationType(AppointmentNotificationType.APPOINTMENT_DELIVERED_LATE);
        dto.setDeliveryTime(DateTime.now().getMillis());
        dto.setAppoitnmentTime(DateTime.now().minusDays(2).getMillis());
        ObjectMapper obj=new ObjectMapper();
        String str =obj.writeValueAsString(dto);
        processMessage(str);
    }


}
