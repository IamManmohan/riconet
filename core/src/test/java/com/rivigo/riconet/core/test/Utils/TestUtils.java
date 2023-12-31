package com.rivigo.riconet.core.test.Utils;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TemplateV2DTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsDTO;

public class TestUtils {
  public static ZoomCommunicationsDTO getDummyZoomCommunicationSmsDto(
      String phoneNumber, String message, String templateV2, String notificationDto) {
    ZoomCommunicationsDTO dto = new ZoomCommunicationsDTO();
    dto.setPhoneNumber(phoneNumber);
    dto.setMessage(message);
    dto.setTemplateV2(templateV2);
    dto.setNotificationDTO(notificationDto);
    return dto;
  }

  public static NotificationDTO getDummyNotificationDto(String eventName, Boolean isTemplateV2) {
    return NotificationDTO.builder().eventName(eventName).isTemplateV2(isTemplateV2).build();
  }

  public static TemplateV2DTO getDummyTemplateV2Dto(String name) {
    return TemplateV2DTO.builder().name(name).build();
  }
}
