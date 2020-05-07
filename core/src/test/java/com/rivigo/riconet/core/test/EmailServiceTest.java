package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.EmailServiceImpl;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class EmailServiceTest {

  private static final String notificationRootUrl = "http://notification-root-url.dummy.com";
  private static final String emailApi = "/test/email/api";

  @InjectMocks private EmailServiceImpl emailService;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private RestTemplate restTemplate;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(emailService, "notificationRootUrl", notificationRootUrl);
    ReflectionTestUtils.setField(emailService, "emailApi", emailApi);
    when(restTemplate.exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class)))
        .thenReturn(null);
    when(zoomPropertyService.getBoolean(ZoomPropertyName.EMAIL_ENABLED, false)).thenReturn(true);
  }

  @Test
  public void sendMailEmptyProductionTest() {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
    emailService.sendEmail(
        "sender@rivigo.com",
        Collections.singleton("dummy@rivigo.com"),
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        "subject",
        "body",
        null);
    Mockito.verify(restTemplate, times(1))
        .exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class));
  }

  @Test
  public void sendMailEmptySubjectTest() {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
    emailService.sendEmail(
        "sender@rivigo.com",
        Collections.singleton("dummy@rivigo.com"),
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        "",
        "body",
        null);
    Mockito.verify(restTemplate, times(0))
        .exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class));
  }

  @Test
  public void sendMailEmptyRecipientsTest() {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
    emailService.sendEmail(
        "sender@rivigo.com",
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        "subject",
        "body",
        null);
    Mockito.verify(restTemplate, times(0))
        .exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class));
  }

  @Test
  public void sendMailStagingEmptyDefaultRecipientsTest() {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "staging");
    emailService.sendEmail(
        "sender@rivigo.com",
        Collections.singleton("dummy@rivigo.com"),
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        "subject",
        "body",
        null);
    Mockito.verify(restTemplate, times(0))
        .exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class));
  }

  @Test
  public void sendMailStagingTest() throws IOException {
    System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "staging");
    when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_EMAIL_IDS))
        .thenReturn("developer@rivigo.com");

    List<String> recipients = new ArrayList<>();
    recipients.add("dummy@rivigo.com");

    File file = File.createTempFile("test", ".xlsx");
    emailService.sendEmail(
        "sender@rivigo.com",
        recipients,
        Collections.EMPTY_LIST,
        Collections.EMPTY_LIST,
        "subject",
        "body",
        file);
    Mockito.verify(restTemplate, times(1))
        .exchange(
            eq(notificationRootUrl + emailApi), eq(HttpMethod.POST), any(), eq(JsonNode.class));
  }
}
