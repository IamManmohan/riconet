package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ZoomCommunicationsSMSDTO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by aditya on 22/2/18.
 */
@Slf4j
@Service
public class ZoomCommunicationsService {

  //This is usually in evening
  public Integer dndStartTime = 18;

  //This is usually in morning
  public Integer dndEndTime = 8;

  @Autowired
  SmsService smsService;

  @Autowired
  private ObjectMapper objectMapper;

  public void processNotificationMessage(ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO) {

    log.info("Processing zoomCommunicationsSMSDTO");
    if (null == zoomCommunicationsSMSDTO) {
      log.debug("zoomCommunicationsSMSDTO is null");
      return;
    }

    if (StringUtils.isEmpty(zoomCommunicationsSMSDTO.getPhoneNumber())) {
      log.debug("zoomCommunicationsSmsDTO with empty or null phonenumbers");
      return;
    }

    log.info("Sending sms, message {}, on Phone number {}",
        zoomCommunicationsSMSDTO.getMessage(),
        zoomCommunicationsSMSDTO.getPhoneNumber());


    log.debug("DND start time {} and end time {}", dndStartTime, dndEndTime);
    int hourOfDay = DateTime.now().withZone(DateTimeZone.forOffsetHoursMinutes(5, 30)).getHourOfDay();
    if (hourOfDay >= dndEndTime && hourOfDay < dndStartTime) {
      String returnValue = smsService.sendSms("7795569771", zoomCommunicationsSMSDTO.getMessage());
      log.info("Return value from notificationService {}", returnValue);
    } else {
      log.info("Can not send sms as the current time is dnd time");
    }
  }

}
