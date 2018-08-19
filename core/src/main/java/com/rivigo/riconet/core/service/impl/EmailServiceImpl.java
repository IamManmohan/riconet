package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.AttachmentDto;
import com.rivigo.riconet.core.dto.SendEmailRequestDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.model.EmailDL;
import com.rivigo.zoom.common.repository.mysql.EmailDLRepository;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

  @Value("${notification.root.url}")
  private String notificationRootUrl;

  @Value("${notification.email.api}")
  private String emailApi;

  @Autowired private EmailDLRepository emailDLRepository;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private RestTemplate restTemplate;

  @Override
  public Set<String> getEmails(EmailDlName dl) {
    return emailDLRepository
        .findByDlAndIsActive(dl, 1)
        .stream()
        .map(EmailDL::getEmail)
        .collect(Collectors.toSet());
  }

  @Override
  public void sendEmail(
      String from,
      Collection<String> inputRecipients,
      Collection<String> inputCc,
      Collection<String> inputBcc,
      String subject,
      String body,
      File f) {
    Boolean emailEnabled = zoomPropertyService.getBoolean(ZoomPropertyName.EMAIL_ENABLED, false);

    Collection<String> recipients = inputRecipients;
    Collection<String> cc = inputCc;
    Collection<String> bcc = inputBcc;
    log.info(
        "from: {}, recipients: {}, cc: {}, bcc: {}, subject: {}, body: {}, file: {}",
        from,
        recipients,
        cc,
        bcc,
        subject,
        body,
        f == null ? null : f.getName());
    if (!emailEnabled || CollectionUtils.isEmpty(recipients) || StringUtils.isBlank(subject)) {
      log.info("Not sending mail due to incomplete details");
      return;
    }
    String finalBody = StringUtils.isBlank(body) ? "No Content" : body;
    if (!"production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      String defaultEmails = zoomPropertyService.getString(ZoomPropertyName.DEFAULT_EMAIL_IDS);
      log.info("defaultEmails: {}", defaultEmails);
      if (defaultEmails == null) {
        return;
      }
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder
          .append("recipients: ")
          .append(recipients)
          .append("<br> cc: ")
          .append(cc)
          .append("<br> bcc: ")
          .append(bcc)
          .append("<br><br>")
          .append(finalBody);
      finalBody = stringBuilder.toString();
      recipients = Arrays.asList(defaultEmails.split(","));
      cc = Collections.emptyList();
      bcc = Collections.emptyList();
    }
    try {
      SendEmailRequestDTO request = new SendEmailRequestDTO();
      request.setFrom(from);
      request.setTo(new ArrayList<>(recipients));
      request.setCc(new ArrayList<>(cc));
      request.setBcc(new ArrayList<>(bcc));
      request.setSubject(subject);
      request.setBody(finalBody);
      if (f != null) {
        AttachmentDto attachmentDto = new AttachmentDto();
        attachmentDto.setData(Files.readAllBytes(f.toPath()));
        attachmentDto.setName(f.getName());
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        attachmentDto.setType(mimetypesFileTypeMap.getContentType(f.getName()));
        request.setAttachmentList(Collections.singletonList(attachmentDto));
      }
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity entity = new HttpEntity<>(request, headers);
      log.info("Email url: {}, Entity {}",notificationRootUrl + emailApi,entity);
      ResponseEntity<JsonNode> response =
          restTemplate.exchange(
              notificationRootUrl + emailApi, HttpMethod.POST, entity, JsonNode.class);
      log.info("email api response: {}", response);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
