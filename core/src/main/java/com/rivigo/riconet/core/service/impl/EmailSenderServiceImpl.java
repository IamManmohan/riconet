package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.AttachmentDto;
import com.rivigo.riconet.core.dto.NotificationResponseDTO;
import com.rivigo.riconet.core.dto.SendEmailRequestDTO;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.ZoomProperty;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

  private final ZoomPropertyService zoomPropertyService;

  @Autowired
  public EmailSenderServiceImpl(ZoomPropertyService zoomPropertyService) {
    this.restTemplate = new RestTemplate();
    this.zoomPropertyService = zoomPropertyService;
  }

  @Override
  public void sendEmail(List<String> recipients, String subject, String body) {
    sendEmail(recipients, subject, body, null, null);
  }

  @Override
  public void sendEmail(
      List<String> recipients, String subject, String body, MultipartFile file, String type) {
    SendEmailRequestDTO request = new SendEmailRequestDTO();
    request.setFrom(senderServerName);

    if ("production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      log.info("sending mail to actual recipient on production env : {} ", recipients);
    } else {
      ZoomProperty zoomProperty =
          zoomPropertyService.getByPropertyName(ZoomPropertyName.DEFAULT_EMAIL_IDS.name());
      if (null == zoomProperty || StringUtils.isEmpty(zoomProperty.getVariableValue())) {
        log.info("Not sending any mail as not recipient found");
        return;
      } else {
        recipients = Arrays.asList(zoomProperty.getVariableValue().split(","));
        log.info("sending mail on staging env to default users : {} ", recipients);
      }
    }

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
      SendEmailRequestDTO request, List<String> recipients, String subject, String body) {
    try {

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-User-Agent", emailUserAgent);
      HttpEntity<SendEmailRequestDTO> entity = new HttpEntity<>(request, headers);
      log.info(
          "senderServerName : {} ,  emailServiceApi : {} , emailUserAgent : {} ",
          senderServerName,
          emailServiceApi,
          emailUserAgent);
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
