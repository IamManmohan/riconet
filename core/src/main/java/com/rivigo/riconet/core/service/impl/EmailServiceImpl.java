package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.model.EmailDL;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import com.rivigo.zoom.common.repository.mysql.EmailDLRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    sendEmail(
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
    sendEmail(
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
    sendEmail(
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

  /** Replace by notification service */
  @Deprecated
  private static void sendEmail(
      String user,
      String password,
      List<String> toRecipients,
      List<String> ccRecipients,
      List<String> bccRecipients,
      String subject,
      String body,
      File attachment) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.office365.com");
    mailSender.setPort(587);
    mailSender.setUsername(user);
    mailSender.setPassword(password);

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.debug", "true");
    javaMailProperties.put("mail.smtp.auth", "true");
    javaMailProperties.put("mail.smtp.starttls.enable", "true");
    mailSender.setJavaMailProperties(javaMailProperties);

    MimeMessage message = mailSender.createMimeMessage();
    try {
      message.setFrom(new InternetAddress(user));

      addRecipients(message, Message.RecipientType.TO, toRecipients);
      addRecipients(message, Message.RecipientType.CC, ccRecipients);
      addRecipients(message, Message.RecipientType.BCC, bccRecipients);
      message.setSubject(subject);

      Multipart multipart = new MimeMultipart();

      BodyPart textBodyPart = new MimeBodyPart();
      textBodyPart.setText(body);
      textBodyPart.setContent(body, "text/html");
      multipart.addBodyPart(textBodyPart);

      if (null != attachment) {
        BodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.setDataHandler(new DataHandler(new FileDataSource(attachment)));
        attachmentBodyPart.setFileName(attachment.getName());
        multipart.addBodyPart(attachmentBodyPart);
      }
      message.setContent(multipart, "text/html; charset=utf-8");

      mailSender.send(message);
    } catch (MessagingException e) {
      log.error("Error sending mail", e);
    }
  }

  private static void addRecipients(
      MimeMessage message, Message.RecipientType recipientType, List<String> recipients)
      throws MessagingException {
    if (!CollectionUtils.isEmpty(recipients)) {
      List<Address> recipientAddresses = new ArrayList<>();
      for (String recipient : recipients) {
        recipientAddresses.add(new InternetAddress(recipient));
      }
      message.addRecipients(
          recipientType, recipientAddresses.toArray(new Address[recipientAddresses.size()]));
    }
  }
}
