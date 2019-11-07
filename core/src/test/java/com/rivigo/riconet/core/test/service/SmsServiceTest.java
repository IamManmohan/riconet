package com.rivigo.riconet.core.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.TemplateV2DTO;
import com.rivigo.riconet.core.dto.platformteam.SendSmsV2ResponseDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.SmsServiceImpl;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

public class SmsServiceTest {
  @InjectMocks private SmsServiceImpl smsService;

  @Mock private ZoomPropertyService zoomPropertyService;
  @Mock private ObjectMapper objectMapper;
  @Mock private RestClientUtilityService restClientUtilityService;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private static final String mobileNumber = TestConstants.phoneNumber;
  private static final String message = "send sms";
  private static final String rootUrl = "http://send-sms-base-url";
  private static final String smsApi = "/sendSms/v1";
  private static final String SMS_SERVER_URL_ABSENT = "sms server url is absent";
  private static final String SMS_STRING_ABSENT = "sms string is absent";

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(smsService, "smsEnable", true);
    ReflectionTestUtils.setField(smsService, "rootUrl", rootUrl);
    ReflectionTestUtils.setField(smsService, "smsApi", smsApi);
  }

  @Test
  public void sendSmsTest1() {
    Assert.assertEquals(SMS_STRING_ABSENT, smsService.sendSms(mobileNumber, ""));
    ReflectionTestUtils.setField(smsService, "rootUrl", "");
    Assert.assertEquals(SMS_SERVER_URL_ABSENT, smsService.sendSms(mobileNumber, message));
  }

  @Test
  public void sendSmsTest2() {
    ObjectMapper om = new ObjectMapper();
    Mockito.when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, ""))
        .thenReturn(mobileNumber);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());
    Mockito.when(objectMapper.createObjectNode()).thenReturn(om.createObjectNode());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.empty());

    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("SMS is not sent properly");
    smsService.sendSms(mobileNumber, message);
  }

  @Test
  public void sendSmsTest3() {
    ObjectMapper om = new ObjectMapper();
    Mockito.when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, ""))
        .thenReturn(mobileNumber);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());
    Mockito.when(objectMapper.createObjectNode()).thenReturn(om.createObjectNode());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(new Object()));

    String response = smsService.sendSms(mobileNumber, message);
    Assert.assertFalse(StringUtils.isBlank(response));
  }

  @Test
  public void validateSendSmsTest() {
    Pair<Boolean, String> responsePair1 =
        ReflectionTestUtils.invokeMethod(smsService, "validateSendSms", "", rootUrl);
    Assert.assertFalse(responsePair1.getFirst());
    Assert.assertEquals("invalid recipients", responsePair1.getSecond());

    ReflectionTestUtils.setField(smsService, "smsEnable", false);
    Pair<Boolean, String> responsePair2 =
        ReflectionTestUtils.invokeMethod(smsService, "validateSendSms", mobileNumber, rootUrl);
    Assert.assertFalse(responsePair2.getFirst());
    Assert.assertEquals("sending sms is disabled", responsePair2.getSecond());
  }

  @Test
  public void sendSmsV2Test1() {
    Assert.assertFalse(smsService.sendSmsV2("", null));
    Assert.assertFalse(smsService.sendSmsV2(mobileNumber, null));

    TemplateV2DTO template = TemplateV2DTO.builder().build();
    Mockito.when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, ""))
        .thenReturn(mobileNumber);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.empty());
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        String.format(
            "send sms failed for phoneNumbers: {}, templateName: {}",
            Collections.singleton(mobileNumber),
            template.getName()));
    smsService.sendSmsV2(mobileNumber, template);
  }

  @Test
  public void sendSmsV2Test2() {
    SendSmsV2ResponseDTO smsResponse = new SendSmsV2ResponseDTO();
    smsResponse.setBulkResponse(Collections.emptyList());
    TemplateV2DTO template = TemplateV2DTO.builder().build();

    Mockito.when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, ""))
        .thenReturn(mobileNumber);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(new Object()));
    Mockito.when(objectMapper.convertValue(Mockito.any(), Mockito.eq(SendSmsV2ResponseDTO.class)))
        .thenReturn(smsResponse);

    Assert.assertTrue(smsService.sendSmsV2(mobileNumber, template));
  }

  @Test
  public void sendSmsV2Test3() {
    TemplateV2DTO template = TemplateV2DTO.builder().build();

    Mockito.when(zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, ""))
        .thenReturn(mobileNumber);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(new Object()));
    Mockito.when(objectMapper.convertValue(Mockito.any(), Mockito.eq(SendSmsV2ResponseDTO.class)))
        .thenReturn(new SendSmsV2ResponseDTO());

    Assert.assertFalse(smsService.sendSmsV2(mobileNumber, template));
  }
}
