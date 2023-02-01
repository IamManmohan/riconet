package com.rivigo.riconet.core.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.EmailConstant;
import com.rivigo.riconet.core.dto.ClientContactDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.CMSService;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomCommunicationEmailService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import com.rivigo.riconet.core.test.Utils.TestUtils;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ZoomCommunicationEmailServiceTest {

  @InjectMocks private ZoomCommunicationEmailService zoomCommunicationsEmailService;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private ObjectMapper objectMapper;

  @Mock private ClientMasterService clientMasterService;

  @Mock private CMSService cmsService;

  @Mock private EmailService emailService;

  @Mock private UserMasterService userMasterService;

  @Before
  public void intiMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processNotificationSendMessageTestCorporateConsignnee() throws IOException {
    String phoneNumber = TestConstants.phoneNumber;
    String message = "send sms";
    NotificationDTO notificationDto =
        TestUtils.getDummyNotificationDto(TestConstants.eventName, null);
    notificationDto.setConditions(Collections.singletonList("CLIENTS_CORPORATE"));

    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CLIENT_ID.name(), "1519");
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), "12345678");

    notificationDto.setMetadata(metadata);

    ZoomCommunicationsDTO zoomCommunicationsDTO =
        TestUtils.getDummyZoomCommunicationSmsDto(
            phoneNumber, message, null, notificationDto.toString());

    Mockito.when(objectMapper.readValue(notificationDto.toString(), NotificationDTO.class))
        .thenReturn(notificationDto);
    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.DND_EXEMPTED_SMS_EVENTS))
        .thenReturn(Collections.singletonList(TestConstants.eventName));

    zoomCommunicationsEmailService.processNotification(zoomCommunicationsDTO);
    Mockito.verify(emailService, Mockito.times(0))
        .sendEmail(
            EmailConstant.SERVICE_EMAIL_ID,
            Collections.singletonList("abc@rivigo.com"),
            Collections.singletonList("pqr@rivigo.com"),
            new ArrayList<>(),
            String.format(EmailConstant.CN_UPDATE_EMAIL_SUBJECT_TEMPLATE, 12345678),
            message,
            null);
    Assert.assertNull(notificationDto.getIsTemplateV2());
  }

  @Test
  public void processNotificationSendMessageTestCorporateConsignner() throws IOException {
    String phoneNumber = TestConstants.phoneNumber;
    String message = "email sms";
    NotificationDTO notificationDto =
        TestUtils.getDummyNotificationDto(TestConstants.eventName, null);
    notificationDto.setConditions(Collections.singletonList("CLIENTS_CORPORATE"));

    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CLIENT_ID.name(), "1519");
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), "12345678");
    notificationDto.setMetadata(metadata);

    ZoomCommunicationsDTO zoomCommunicationsDTO =
        TestUtils.getDummyZoomCommunicationSmsDto(
            phoneNumber, message, null, notificationDto.toString());
    zoomCommunicationsDTO.setUserType(ClientConstants.CONSIGNER_VALUE);

    Client client = new Client();
    client.setClientCode("SEPLD");
    client.setSamUserId(1234L);

    ClientContactDTO clientContact = new ClientContactDTO();
    clientContact.setEmail("abc@rivigo.com");
    clientContact.setType(ClientConstants.SERVICE_POC_STRING);
    clientContact.setLevel(ClientConstants.POC_LEVEL_FOR_EMAIL);

    User samUser = new User();
    samUser.setEmail("pqr@rivigo.com");

    Mockito.when(objectMapper.readValue(notificationDto.toString(), NotificationDTO.class))
        .thenReturn(notificationDto);
    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.DND_EXEMPTED_SMS_EVENTS))
        .thenReturn(Collections.singletonList(TestConstants.eventName));
    Mockito.when(clientMasterService.getClientById(1519L)).thenReturn(client);
    Mockito.when(cmsService.getClientContacts("SEPLD"))
        .thenReturn(Collections.singletonList(clientContact));
    Mockito.when(userMasterService.getById(1234L)).thenReturn(samUser);

    zoomCommunicationsEmailService.processNotification(zoomCommunicationsDTO);
    Mockito.verify(emailService, Mockito.times(1))
        .sendEmail(
            EmailConstant.SERVICE_EMAIL_ID,
            Collections.singletonList("abc@rivigo.com"),
            Collections.singletonList("pqr@rivigo.com"),
            new ArrayList<>(),
            String.format(EmailConstant.CN_UPDATE_EMAIL_SUBJECT_TEMPLATE, 12345678),
            message,
            null);
    Assert.assertNull(notificationDto.getIsTemplateV2());
  }
}
