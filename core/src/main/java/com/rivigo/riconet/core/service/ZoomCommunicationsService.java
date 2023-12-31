package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TemplateV2DTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsDTO;
import com.rivigo.riconet.core.enums.Clients;
import com.rivigo.riconet.core.utils.ConsignmentUtils;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by aditya on 22/2/18. */
@Slf4j
@Service
public class ZoomCommunicationsService {

  @Autowired private SmsService smsService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ObjectMapper objectMapper;

  public void processNotification(ZoomCommunicationsDTO zoomCommunicationsDTO) {

    // This is usually in evening
    Integer dndStartTime =
        zoomPropertyService.getInteger(
            ZoomPropertyName.ZOOM_COMMUNICATION_DND_START_TIME, 1000 * 20 * 60 * 60);
    // This is usually in morning
    Integer dndEndTime =
        zoomPropertyService.getInteger(
            ZoomPropertyName.ZOOM_COMMUNICATION_DND_END_TIME, 1000 * 8 * 60 * 60);

    log.info("Processing zoomCommunicationsSMSDTO");
    if (null == zoomCommunicationsDTO) {
      log.debug("zoomCommunicationsSMSDTO is null");
      return;
    }

    if (StringUtils.isEmpty(zoomCommunicationsDTO.getPhoneNumber())) {
      log.debug("zoomCommunicationsSmsDTO with empty or null phonenumbers");
      return;
    }

    boolean isDndExempted = false;
    // To Note: Not all events may have been added to EventName so deserialization for those will
    // fail
    // and exemption will not work by adding new events only to zoom property.
    // Best solution: Single common list be maintained in a communications commons

    TemplateV2DTO templateV2 = null;
    Boolean isTemplateV2 = false;
    Boolean isCorporate = false;
    try {
      NotificationDTO notificationDTO =
          objectMapper.readValue(zoomCommunicationsDTO.getNotificationDTO(), NotificationDTO.class);
      isCorporate =
          Optional.ofNullable(notificationDTO.getConditions())
              .orElse(new ArrayList<>())
              .contains(Clients.CORPORATE.toString());
      String templateString = zoomCommunicationsDTO.getTemplateV2();
      templateV2 =
          StringUtils.isBlank(templateString)
              ? null
              : objectMapper.readValue(templateString, TemplateV2DTO.class);
      isTemplateV2 = notificationDTO.getIsTemplateV2();
      List<String> dndExemptedEvents =
          zoomPropertyService.getStringValues(ZoomPropertyName.DND_EXEMPTED_SMS_EVENTS);
      isDndExempted = dndExemptedEvents.contains(notificationDTO.getEventName());
      log.debug("NotificationDTO {}", notificationDTO);
      log.debug("TemplateV2 is {}", templateV2);
    } catch (IOException ex) {
      log.error(
          "Error occured while processing NotificationDTO for {} ",
          zoomCommunicationsDTO.getEventUID(),
          ex);
    }

    if (ConsignmentUtils.SHOULD_SEND_EMAIL.negate().test(isCorporate, zoomCommunicationsDTO)) {
      log.info(
          "Sending sms, message {}, templateV2 {}, on Phone number {}",
          zoomCommunicationsDTO.getMessage(),
          zoomCommunicationsDTO.getTemplateV2(),
          zoomCommunicationsDTO.getPhoneNumber());

      log.debug(
          "DND start time {} and end time {}, isDndExempted: {}",
          dndStartTime,
          dndEndTime,
          isDndExempted);
      int millisOfDay =
          DateTime.now().withZone(DateTimeZone.forOffsetHoursMinutes(5, 30)).getMillisOfDay();
      if (isDndExempted || (millisOfDay >= dndEndTime && millisOfDay < dndStartTime)) {
        log.info("Value of IsTemplateV2 flag is {}", isTemplateV2);
        if (Boolean.TRUE.equals(isTemplateV2)) {
          smsService.sendSmsV2(zoomCommunicationsDTO.getPhoneNumber(), templateV2);
        } else {
          String returnValue =
              smsService.sendSms(
                  zoomCommunicationsDTO.getPhoneNumber(), zoomCommunicationsDTO.getMessage());
          log.info("send sms response is {}", returnValue);
        }
      } else {
        log.info("Can not send sms as the current time is dnd time");
      }
    } else {
      log.info(
          "Not Sending SMS as the event is for Corporate Client Consignor, eventUID {}",
          zoomCommunicationsDTO.getEventUID());
    }
  }
}
