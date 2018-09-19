package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.EmailDlName;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

  Set<String> getEmails(EmailDlName dl);

  void sendEmail(
      String email,
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file);
}
