package com.rivigo.riconet.core.service.impl;

import com.rivigo.notification.common.dto.AttachmentDto;
import com.rivigo.notification.common.request.SendEmailRequest;
import com.rivigo.riconet.core.dto.NotificationResponseDTO;
import com.rivigo.riconet.core.service.EmailSenderService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ramesh
 * @date 16-Aug-2018
 */
@Service
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

  // how to set local_mail.properties
  //  #mail properties
  //
  // email.notification.service.api=http://rivigonotifications-stg.ap-southeast-1.elasticbeanstalk.com//api/v1/email/send
  //  sender.server.name=testing@devops.rivigo.com
  //  email.notification.service.user.agent=zoom-ticketing-dev
  //  ticketing.master.email=zoom.ticketing@rivigo.com
  //  ticketing.master.password=Ticketing@Z00m

  @Value("${sender.server.name}")
  private String senderServerName;

  @Value("${email.notification.service.api}")
  private String emailServiceApi;

  @Value("${email.notification.service.user.agent}")
  private String emailUserAgent;

  private RestTemplate restTemplate;

  @Autowired
  public EmailSenderServiceImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Autowired
  public void sendEmail(List<String> recipients, String subject, String body) {
    sendEmail(recipients, subject, body, null, null);
  }

  @Override
  public void sendEmail(
      List<String> recipients, String subject, String body, MultipartFile file, String type) {
    SendEmailRequest request = new SendEmailRequest();
    request.setFrom(senderServerName);
    request.setTo(recipients);
    request.setSubject(subject);
    request.setBody(body);

    if (file != null) {
      AttachmentDto attachmentDto = new AttachmentDto();
      attachmentDto.setName(file.getName());
      attachmentDto.setType(type);
      try {
        attachmentDto.setData(file.getBytes());
      } catch (IOException e) {
        log.error("Error converting attachments to bytes");
      }
      request.setAttachmentList(Collections.singletonList(attachmentDto));
    }
    send(request, recipients, subject, body);
  }

  private void send(
      SendEmailRequest request, List<String> recipients, String subject, String body) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-User-Agent", emailUserAgent);
      HttpEntity<SendEmailRequest> entity = new HttpEntity<>(request, headers);
      ResponseEntity<NotificationResponseDTO> responseObject =
          restTemplate.exchange(
              emailServiceApi, HttpMethod.POST, entity, NotificationResponseDTO.class);
      log.info("Mail sent to recipients: {} , subject: {} , body: {} ", recipients, subject, body);
      log.info("Email response is {} ", responseObject);
    } catch (Exception e) {
      log.error(
          " Error while sending mail to recipients: {} with subject: {} and body: {} ",
          recipients,
          subject,
          body);
      log.error("error in sending mail {} e", request, e);
    }
  }
}
