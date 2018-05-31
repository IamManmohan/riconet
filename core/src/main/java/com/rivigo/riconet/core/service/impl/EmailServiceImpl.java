package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.model.EmailDL;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import com.rivigo.zoom.common.repository.mysql.EmailDLRepository;
import com.rivigo.zoom.common.utils.MailUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

  public static final String SHORTAGE_EMAIL_ID = "shortage.desk@rivigo.com";
  public static final String SHORTAGE_EMAIL_PASS = "deps@1122";

  public static final String DOCUMENT_EMAIL_ID = "document.desk@rivigo.com";
  public static final String DOCUMENT_EMAIL_PASS = "Rivigo@666";

  public static final String APPOINTMENT_EMAIL_ID = "appointment@rivigo.com";
  public static final String APPOINTMENT_EMAIL_PASS = "rivigo@123";

  @Autowired private EmailDLRepository emailDLRepository;

  @Override
  public void sendShortageEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file) {
    MailUtils.sendEmail(
        SHORTAGE_EMAIL_ID,
        SHORTAGE_EMAIL_PASS,
        new ArrayList<>(toRecipients),
        new ArrayList<>(ccRecipients),
        new ArrayList<>(bccRecipients),
        subject,
        body,
        file);
  }

  @Override
  public Set<String> getEmails(EmailDlName dl) {
    return emailDLRepository
        .findByDlAndIsActive(dl, 1)
        .stream()
        .map(EmailDL::getEmail)
        .collect(Collectors.toSet());
  }

  @Override
  public void sendDocumentIssueEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file) {
    MailUtils.sendEmail(
        DOCUMENT_EMAIL_ID,
        DOCUMENT_EMAIL_PASS,
        new ArrayList<>(toRecipients),
        new ArrayList<>(ccRecipients),
        new ArrayList<>(bccRecipients),
        subject,
        body,
        file);
  }

  @Override
  public void sendAppointmentEmail(
      Collection<String> toRecipients,
      Collection<String> ccRecipients,
      Collection<String> bccRecipients,
      String subject,
      String body,
      File file) {
    MailUtils.sendEmail(
        APPOINTMENT_EMAIL_ID,
        APPOINTMENT_EMAIL_PASS,
        new ArrayList<>(toRecipients),
        new ArrayList<>(ccRecipients),
        new ArrayList<>(bccRecipients),
        subject,
        body,
        file);
  }

  @Override
  public void filterEmails(AbstractMailNotificationEntity dto, Set<String> bccList) {
    dto.getBccList().addAll(bccList);
    if ("production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      return;
    }
    List<String> dummyEmailList = new ArrayList<>();
    dto.getEmailIdList()
        .forEach(email -> dummyEmailList.add(email.split("@")[0] + "@rivigodummy.com"));
    dto.getEmailIdList().clear();
    dto.getEmailIdList().addAll(dummyEmailList);

    List<String> dummyCcList = new ArrayList<>();
    dto.getCcList().forEach(email -> dummyCcList.add(email.split("@")[0] + "@rivigodummy.com"));
    dto.getCcList().clear();
    dto.getCcList().addAll(dummyCcList);
  }
}
