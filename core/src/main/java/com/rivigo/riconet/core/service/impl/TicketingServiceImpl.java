package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.FieldName;
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
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCreationEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketAssigneeChangeEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketAssigneeChangeEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketStatusChangeEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketStatusChangeEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketEscalationChangeEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketEscalationChangeEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketCcNewPersonAdditionEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCcNewPersonAdditionEmailBody(metadata);
    TicketingEmailTemplateHelper.getValueFromMap(metadata, FieldName.Ticketing.NEWLY_CCED_EMAIL)
        .ifPresent(
            to -> emailSenderService.sendEmail(Collections.singletonList(to), subject, body));
  }

  @Override
  public void sendTicketSeverityChangeEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketSeverityChangeEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void sendTicketCommentCreationEmail(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    Optional<List<String>> toRecipients = TicketingEmailTemplateHelper.getRecipientList(metadata);
    String subject = TicketingEmailTemplateHelper.getSubject(metadata);
    String body = TicketingEmailTemplateHelper.getTicketCommentCreationEmailBody(metadata);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }
}
