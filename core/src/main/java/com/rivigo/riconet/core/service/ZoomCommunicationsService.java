package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ZoomCommunicationsSMSDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** Created by aditya on 22/2/18. */
@Slf4j
@Service
public class ZoomCommunicationsService {

  @Autowired private SmsService smsService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  public void processNotificationMessage(ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO) {

    // This is usually in evening
    Integer dndStartTime = zoomPropertyService.getInteger(ZoomPropertyName.ZOOM_COMMUNICATION_DND_START_TIME, 1000 * 20 * 60 * 60);
    // This is usually in morning
    Integer dndEndTime = zoomPropertyService.getInteger(ZoomPropertyName.ZOOM_COMMUNICATION_DND_END_TIME, 1000 * 8 * 60 * 60);

    log.info("Processing zoomCommunicationsSMSDTO");
    if (null == zoomCommunicationsSMSDTO) {
      log.debug("zoomCommunicationsSMSDTO is null");
      return;
    }

    if (StringUtils.isEmpty(zoomCommunicationsSMSDTO.getPhoneNumber())) {
      log.debug("zoomCommunicationsSmsDTO with empty or null phonenumbers");
      return;
    }

    log.info(
        "Sending sms, message {}, on Phone number {}", zoomCommunicationsSMSDTO.getMessage(), zoomCommunicationsSMSDTO.getPhoneNumber());

    log.debug("DND start time {} and end time {}", dndStartTime, dndEndTime);
    int millisOfDay = DateTime.now().withZone(DateTimeZone.forOffsetHoursMinutes(5, 30)).getMillisOfDay();
    if (millisOfDay >= dndEndTime && millisOfDay < dndStartTime) {
      String returnValue = smsService.sendSms(zoomCommunicationsSMSDTO.getPhoneNumber(), zoomCommunicationsSMSDTO.getMessage());
      log.info("Return value from notificationService {}", returnValue);
    } else {
      log.info("Can not send sms as the current time is dnd time");
    }
  }
}
