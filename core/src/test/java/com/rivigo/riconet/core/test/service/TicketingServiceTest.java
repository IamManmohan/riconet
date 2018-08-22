package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
public class TicketingServiceTest {

  private TicketingServiceImpl ticketingService;

  private RestTemplate restTemplate;

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
    EmailSenderService emailSenderService = new EmailSenderServiceImpl(restTemplate);
    ticketingService = new TicketingServiceImpl(emailSenderService);
    ReflectionTestUtils.setField(
        emailSenderService, "senderServerName", "testing@devops.rivigo.com");
    ReflectionTestUtils.setField(
        emailSenderService,
        "emailServiceApi",
        "http://rivigonotifications-stg.ap-southeast-1.elasticbeanstalk.com//api/v1/email/send");
    ReflectionTestUtils.setField(emailSenderService, "emailUserAgent", "zoom-ticketing-dev");
  }

  @Test
  public void sendTicketCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    ticketingService.sendTicketCreationEmail(notificationDTO);
  }

  @Test
  public void sendTicketAssigneeChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTOTICKET_ASSIGNEE_CHANGE();
    ticketingService.sendTicketAssigneeChangeEmail(notificationDTO);
  }

  @Test
  public void sendTicketStatusChangeEmailTest() {
    NotificationDTO notificationDTO = NotificationDTOModel.getNotificationDTOTICKET_STATUS_CHANGE();
    ticketingService.sendTicketStatusChangeEmail(notificationDTO);
  }

  @Test
  public void sendTicketEscalationChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTOTICKET_ESCALATION_CHANGE();
    ticketingService.sendTicketEscalationChangeEmail(notificationDTO);
  }

  @Test
  public void sendTicketSeverityChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTOTICKET_SEVERITY_CHANGE();
    ticketingService.sendTicketSeverityChangeEmail(notificationDTO);
  }

  @Test
  public void sendTicketCcNewPersonAdditionEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTOForTICKET_CC_NEW_PERSON_ADDITION();
    ticketingService.sendTicketCcNewPersonAdditionEmail(notificationDTO);
  }

  @Test
  public void sendTicketCommentCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTOTICKET_COMMENT_CREATION();
    ticketingService.sendTicketCommentCreationEmail(notificationDTO);
  }
}
