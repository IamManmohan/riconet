package com.rivigo.riconet.core.test.service;

import static com.rivigo.riconet.core.constants.ZoomTicketingConstant.TICKETING_ZOOM_PROPERTY_KEY;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomPropertyServiceImpl;
import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import com.rivigo.zoom.common.model.ZoomProperty;
import com.rivigo.zoom.common.repository.mysql.ZoomPropertiesRepository;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketingServiceTest extends TesterBase {

  private TicketingServiceImpl ticketingService;

  @Mock private ZoomPropertiesRepository zoomPropertiesRepository;

  private ZoomProperty zoomProperty;

  /*
   * There is no assert statement in any of test case in this class.
   * Assertion logic : check if these test cases are sending emails
   * to designation emails(configure TestConstants.CREATOR_EMAIL) with removing mock of restTemplate
   * */

  @Autowired private RestTemplate restTemplate;

  @Before
  public void setUp() {
    //    RestTemplate restTemplate = new RestTemplate();
    ZoomPropertyService zoomPropertyService = new ZoomPropertyServiceImpl();
    EmailSenderService emailSenderService = new EmailSenderServiceImpl(zoomPropertyService);
    ticketingService = new TicketingServiceImpl(emailSenderService);
    ReflectionTestUtils.setField(
        emailSenderService, "senderServerName", "testing@devops.rivigo.com");
    ReflectionTestUtils.setField(
        zoomPropertyService, "zoomPropertiesRepository", zoomPropertiesRepository);
    ReflectionTestUtils.setField(
        emailSenderService,
        "emailServiceApi",
        "http://rivigonotifications-stg.ap-southeast-1.elasticbeanstalk.com//api/v1/email/send");
    ReflectionTestUtils.setField(emailSenderService, "emailUserAgent", "riconet-qa");

    zoomProperty = new ZoomProperty();
    zoomProperty.setVariableName(TICKETING_ZOOM_PROPERTY_KEY);
    zoomProperty.setVariableValue("dnagpal755@gmail.com");
  }

  @Test
  public void sendTicketCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketAssigneeChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ASSIGNEE_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketStatusChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_STATUS_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketEscalationChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_ESCALATION_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketSeverityChangeEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_SEVERITY_CHANGE();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCcNewPersonAdditionEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_CC_NEW_PERSON_ADDITION();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }

  @Test
  public void sendTicketCommentCreationEmailTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDtoForTICKET_COMMENT_CREATION();
    Mockito.when(
            zoomPropertiesRepository.findByVariableNameAndIsActive(TICKETING_ZOOM_PROPERTY_KEY, 1))
        .thenReturn(Collections.singletonList(zoomProperty));
    ticketingService.sendTicketingEventsEmail(notificationDTO);
  }
}
