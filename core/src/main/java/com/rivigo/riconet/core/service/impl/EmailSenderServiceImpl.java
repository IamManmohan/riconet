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

  @Value("${sender.server.name}")
  private String senderServerName;

  @Value("${email.notification.service.api}")
  private String emailServiceApi;

  @Value("${notification.client.code}")
  private String emailUserAgent;

  private final RestTemplate restTemplate;

  @Autowired
  public EmailSenderServiceImpl() {
    this.restTemplate = new RestTemplate();
  }

  @Override
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
      log.info(
          "senderServerName : {} ,  emailServiceApi : {} , emailUserAgent : {} ",
          senderServerName,
          emailServiceApi,
          emailUserAgent);
      //      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<NotificationResponseDTO> responseObject =
          this.restTemplate.exchange(
              emailServiceApi, HttpMethod.POST, entity, NotificationResponseDTO.class);
      log.info(
          "Mail sent from : {} to recipients: {} , subject: {} , body: {} ",
          request.getFrom(),
          recipients,
          subject,
          body);
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
