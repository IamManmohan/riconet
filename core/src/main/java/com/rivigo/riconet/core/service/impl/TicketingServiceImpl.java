package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.utils.TicketingEmailTemplateHelper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
@Slf4j
@Service
public class TicketingServiceImpl implements TicketingService {

  private final EmailSenderService emailSenderService;

  @Autowired
  public TicketingServiceImpl(EmailSenderService emailSenderService) {
    this.emailSenderService = emailSenderService;
  }

  @Override
  public void sendTicketCreationEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCreationEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketAssigneeChangeEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketAssigneeChangeEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketStatusChangeEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketStatusChangeEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketEscalationChangeEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketEscalationChangeEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketCcNewPersonAdditionEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCcNewPersonAdditionEmailBody(metadata);
    Optional<String> to =
        TicketingEmailTemplateHelper.getValueFromMap(metadata, TicketingFieldName.NEWLY_CCED_EMAIL);
    log.info("Sending Ticketing Email. To : {} Subject : {}  Body : {} ", to, subject, body);
    to.ifPresent(
        recipient ->
            emailSenderService.sendEmail(Collections.singletonList(recipient), subject, body));
  }

  @Override
  public void sendTicketSeverityChangeEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketSeverityChangeEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketCommentCreationEmail(NotificationDTO notificationDTO) {
    log.info("Identified Event : {} ", notificationDTO.getEventName());
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info("No metadata found for sending email of Event: {} ", notificationDTO.getEventName());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients =
        TicketingEmailTemplateHelper.getCommentEmailRecipientList(metadata);

    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCommentCreationEmailBody(metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }
}
