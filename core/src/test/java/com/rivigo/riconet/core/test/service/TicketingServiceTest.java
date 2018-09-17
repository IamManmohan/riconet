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
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
public class TicketingServiceTest {

  private TicketingServiceImpl ticketingService;

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
    EmailSenderService emailSenderService = new EmailSenderServiceImpl();
    ticketingService =
        new TicketingServiceImpl(
            emailSenderService,
            zoomBackendAPIClientService,
            zoomPropertyService,
            zoomTicketingAPIClientService);
    ReflectionTestUtils.setField(
        emailSenderService, "senderServerName", "testing@devops.rivigo.com");
    ReflectionTestUtils.setField(
        emailSenderService,
        "emailServiceApi",
        "http://rivigonotifications-stg.ap-southeast-1.elasticbeanstalk.com//api/v1/email/send");
    ReflectionTestUtils.setField(emailSenderService, "emailUserAgent", "riconet-qa");
  }

  @Test
  public void sendTicketCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketAssigneeChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ASSIGNEE_CHANGE();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketStatusChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_STATUS_CHANGE();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketEscalationChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ESCALATION_CHANGE();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketSeverityChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_SEVERITY_CHANGE();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCcNewPersonAdditionEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_CC_NEW_PERSON_ADDITION();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCommentCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_COMMENT_CREATION();
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void closeTicketTest() {
    TicketDTO ticket3 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.NEW)
            .id(2l)
            .build();
    ticketingService.closeTicket(ticket3, "test");
  }

  @Test
  public void setPriorityMappingTest() {
    NotificationDTO notificationDTO = new NotificationDTO();
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

    notificationDTO.setEventName(null);
    ticketingService.setPriorityMapping(notificationDTO);
  }
}
