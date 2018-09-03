package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomBackendAPIClientServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomTicketingAPIClientServiceImpl;
import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author ramesh
 * @date 20-Aug-2018
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class TicketingServiceTest extends TesterBase {


  private TicketingServiceImpl ticketingService;

  /*
   * There is no assert statement in any of test case in this class.
   * Assertion logic : check if these test cases are sending emails
   * to designation emails(configure TestConstants.CREATOR_EMAIL) with removing mock of restTemplate
   * */

  @Autowired private RestTemplate restTemplate;
  @Mock private  ZoomPropertyService zoomPropertyService;

  @Before
  public void setUp() {
    //    RestTemplate restTemplate = new RestTemplate();
    EmailSenderService emailSenderService = new EmailSenderServiceImpl();
    ZoomBackendAPIClientService zoomBackendAPIClientService =new ZoomBackendAPIClientServiceImpl();
    ZoomTicketingAPIClientService zoomTicketingAPIClientService=new ZoomTicketingAPIClientServiceImpl();
    ticketingService = new TicketingServiceImpl(emailSenderService,zoomBackendAPIClientService,zoomPropertyService,zoomTicketingAPIClientService);
    ReflectionTestUtils.setField(
        emailSenderService, "senderServerName", "testing@devops.rivigo.com");
    ReflectionTestUtils.setField(
        emailSenderService,
        "emailServiceApi",
        "http://rivigonotifications-stg.ap-southeast-1.elasticbeanstalk.com//api/v1/email/send");
    ReflectionTestUtils.setField(emailSenderService, "emailUserAgent", "riconet-qa");
    MockitoAnnotations.initMocks(this);
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
  public void setPriorityMappingTest()
  {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO.getMetadata().put(TicketingFieldName.TYPE_ID.toString(), "19");
    ticketingService.setPriorityMapping(notificationDTO);
    List<Long>  priorityTicket= new ArrayList<>();
    priorityTicket.add(19l);
    priorityTicket.add(77l);
    Mockito.when(zoomPropertyService.getLongValues(
        ZoomPropertyName.PRIORITY_TICKET_TYPE)).thenReturn(priorityTicket);



    Mockito.when(zoomPropertyService.getLongValues(
        ZoomPropertyName.AUTOCLOSABLE_PRIORITY_TICKET_TYPE)).thenReturn(Collections.singletonList(77l));

    notificationDTO.getMetadata().put(TicketingFieldName.ENTITY_TYPE.toString(),"CN");
    notificationDTO.getMetadata().put(TicketingFieldName.TYPE_ID.toString(), "77");
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO.setEntityId(null);
    ticketingService.setPriorityMapping(notificationDTO);

    notificationDTO.setEventName(null);
    ticketingService.setPriorityMapping(notificationDTO);



  }
}
