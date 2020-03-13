package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomPropertyServiceImpl;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketingServiceTest {

  private TicketingServiceImpl ticketingService;

  @Mock private ZoomPropertiesRepository zoomPropertiesRepository;

  private ZoomProperty zoomProperty;

  /*
   * There is no assert statement in any of test case in this class.
   * Assertion logic : check if these test cases are sending emails
   * to designation emails(configure TestConstants.CREATOR_EMAIL) with removing mock of restTemplate
   * */

  @Mock private ZoomPropertyService zoomPropertyService;
  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;
  @Mock private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    EmailSenderService emailSenderService = new EmailSenderServiceImpl(this.zoomPropertyService);
    ticketingService =
        new TicketingServiceImpl(
            emailSenderService,
            zoomBackendAPIClientService,
            this.zoomPropertyService,
            zoomTicketingAPIClientService);
    //    RestTemplate restTemplate = new RestTemplate();
    ZoomPropertyService zoomPropertyService = new ZoomPropertyServiceImpl();
    ReflectionTestUtils.setField(
        emailSenderService, "senderServerName", "testing@devops.rivigo.com");
    ReflectionTestUtils.setField(
        zoomPropertyService, "zoomPropertiesRepository", zoomPropertiesRepository);
    ReflectionTestUtils.setField(
        emailSenderService,
        "emailServiceApi",
        "http://notification-dummy-url.com/api/v1/email/send");
    ReflectionTestUtils.setField(emailSenderService, "emailUserAgent", "riconet-qa");

    zoomProperty = new ZoomProperty();
    zoomProperty.setVariableName(ZoomPropertyName.DEFAULT_EMAIL_IDS.name());
  }

  @Test
  public void sendTicketCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketAssigneeChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ASSIGNEE_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketStatusChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_STATUS_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketEscalationChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ESCALATION_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketSeverityChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_SEVERITY_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCcNewPersonAdditionEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_CC_NEW_PERSON_ADDITION();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCommentCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_COMMENT_CREATION();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(
                ZoomPropertyName.DEFAULT_EMAIL_IDS.name(), 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void closeTicketTest() {
    TicketDTO ticket3 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.RETAIL_CHEQUE_BOUNCE_TYPE_ID)
            .status(TicketStatus.NEW)
            .id(2l)
            .build();
    ticketingService.closeTicket(ticket3, "test");
  }

  @Test
  public void setPriorityMappingTest() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEventName(EventName.TICKET_ACTION.name());
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO
        .getMetadata()
        .put(TicketingFieldName.TICKET_TYPE.toString(), "Delayed Delivery");
    ticketingService.setPriorityMapping(notificationDTO);

    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.PRIORITY_TICKET_TYPE))
        .thenReturn(Arrays.asList(""));
    Mockito.when(
            zoomPropertyService.getStringValues(ZoomPropertyName.AUTOCLOSABLE_PRIORITY_TICKET_TYPE))
        .thenReturn(Collections.singletonList("Priority Shipment Special Request"));
    notificationDTO.getMetadata().put(TicketingFieldName.ENTITY_TYPE.toString(), "CN");
    notificationDTO
        .getMetadata()
        .put(TicketingFieldName.TICKET_TYPE.toString(), "Priority Shipment Special Request");
    notificationDTO.getMetadata().put(TicketingFieldName.ENTITY_ID.toString(), "1234567890");
    ticketingService.setPriorityMapping(notificationDTO);

    List<String> priorityTicket = new ArrayList<>();
    priorityTicket.add("Delayed Delivery");
    priorityTicket.add("Priority Shipment Special Request");
    Mockito.when(zoomPropertyService.getStringValues(ZoomPropertyName.PRIORITY_TICKET_TYPE))
        .thenReturn(priorityTicket);
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO.getMetadata().put(TicketingFieldName.ENTITY_ID.toString(), "null");
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO.setEntityId(null);
    ticketingService.setPriorityMapping(notificationDTO);
  }
}
