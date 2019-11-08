package com.rivigo.riconet.core.test.service;

import static com.rivigo.riconet.core.test.Utils.TestConstants.eventName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TemplateV2DTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsSMSDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.ZoomCommunicationsService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import com.rivigo.riconet.core.test.Utils.TestUtils;
import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ZoomCommunicationServiceTest {
  @InjectMocks private ZoomCommunicationsService zoomCommunicationsService;

  @Mock private SmsService smsService;
  @Mock private ZoomPropertyService zoomPropertyService;
  @Mock private ObjectMapper objectMapper;

  @Before
  public void intiMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processNotificationSendMessageTest() throws IOException {
    String phoneNumber = TestConstants.phoneNumber;
    String message = "send sms";
    String sendSmsResponse = "sms sent sucessfully";
    NotificationDTO notificationDto = TestUtils.getDummyNotificationDto(eventName, null);
    ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO =
        TestUtils.getDummyZoomCommunicationSmsDto(
            phoneNumber, message, null, notificationDto.toString());
    Integer dndStartTime = 1000 * 20 * 60 * 60;
    Integer dndEndTime = 1000 * 8 * 60 * 60;

    Mockito.when(zoomPropertyService.getInteger(Mockito.any(), Mockito.eq(dndStartTime)))
        .thenReturn(dndStartTime);
    Mockito.when(zoomPropertyService.getInteger(Mockito.any(), Mockito.eq(dndEndTime)))
        .thenReturn(dndEndTime);
    Mockito.when(objectMapper.readValue(notificationDto.toString(), NotificationDTO.class))
        .thenReturn(notificationDto);
    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.DND_EXEMPTED_SMS_EVENTS))
        .thenReturn(Collections.singletonList(eventName));
    Mockito.when(smsService.sendSms(phoneNumber, message)).thenReturn(sendSmsResponse);

    zoomCommunicationsService.processNotificationMessage(zoomCommunicationsSMSDTO);
    Assert.assertNull(notificationDto.getIsTemplateV2());
  }

  @Test
  public void processNotificationSendMessageV2Test() throws IOException {
    String phoneNumber = TestConstants.phoneNumber;
    String templateName = "crm call did not connect";
    TemplateV2DTO templateV2DTO = TestUtils.getDummyTemplateV2Dto(templateName);
    NotificationDTO notificationDto = TestUtils.getDummyNotificationDto(eventName, true);
    ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO =
        TestUtils.getDummyZoomCommunicationSmsDto(
            phoneNumber, null, templateV2DTO.toString(), notificationDto.toString());
    Integer dndStartTime = 1000 * 20 * 60 * 60;
    Integer dndEndTime = 1000 * 8 * 60 * 60;

    Mockito.when(zoomPropertyService.getInteger(Mockito.any(), Mockito.eq(dndStartTime)))
        .thenReturn(dndStartTime);
    Mockito.when(zoomPropertyService.getInteger(Mockito.any(), Mockito.eq(dndEndTime)))
        .thenReturn(dndEndTime);
    Mockito.when(objectMapper.readValue(notificationDto.toString(), NotificationDTO.class))
        .thenReturn(notificationDto);
    Mockito.when(objectMapper.readValue(templateV2DTO.toString(), TemplateV2DTO.class))
        .thenReturn(templateV2DTO);
    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.DND_EXEMPTED_SMS_EVENTS))
        .thenReturn(Collections.singletonList(eventName));
    Mockito.when(smsService.sendSmsV2(phoneNumber, templateV2DTO)).thenReturn(true);

    zoomCommunicationsService.processNotificationMessage(zoomCommunicationsSMSDTO);
    assert notificationDto.getIsTemplateV2();
  }
}
