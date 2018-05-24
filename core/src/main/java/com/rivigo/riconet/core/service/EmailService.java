package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

  void sendShortageEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file);

  Set<String> getEmails(EmailDlName dl);

  void sendDocumentIssueEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file);

  void sendAppointmentEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file);

  void filterEmails(AbstractMailNotificationEntity dto, Set<String> bccList);
}
