package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import org.springframework.stereotype.Service;


@Service
public interface ConsignmentAppointmentService {

  void processAppointmentNotification(AppointmentNotificationDTO dto);

}
