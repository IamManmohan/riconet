package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rivigo.riconet.core.dto.TemplateDTO;
import com.rivigo.riconet.core.dto.platformteam.SendSmsRequestDTO;
import com.rivigo.riconet.core.dto.platformteam.SendSmsResponseDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SmsServiceImpl implements SmsService {

  public static final String X_USER_AGENT_HEADER = "X-User-Agent";

  private static final String SMS_DISABLED = "sending sms is disabled";
  private static final String SMS_SERVER_URL_ABSENT = "sms server url is absent";
  private static final String SMS_STRING_ABSENT = "sms string is absent";
  private static final String INVALID_RECIPIENTS = "invalid recipients";

  @Value("${notification.root.url}")
  private String rootUrl;

  @Value("${notification.sms.api}")
  private String smsApi;

  @Value("${notification.sms.api.v2}")
  private String smsApiV2;

  @Value("${notification.sms.enable}")
  private Boolean smsEnable;

  @Value("${notification.client.code}")
  private String notificationClientCode;

  @Value("${notification.client.code.v2}")
  private String notificationClientCodeV2;

  private final ZoomPropertyService zoomPropertyService;
  private final ObjectMapper objectMapper;

  @Qualifier("defaultRestClientUtilityServiceImpl")
  private final RestClientUtilityService restClientUtilityService;

  @Override
  @Deprecated
  public String sendSms(String mobileNo, String message) {
    Pair<Boolean, String> validateResponse = validateSendSms(mobileNo, rootUrl);
    if (Boolean.FALSE.equals(validateResponse.getFirst())) {
      return validateResponse.getSecond();
    } else if (StringUtils.isBlank(message)) {
      return SMS_STRING_ABSENT;
    }

    String smsString = getSmsStringBasedOnProfile(message, mobileNo);
    List<String> phoneNumbers = getPhoneNumbersBasedOnProfile(mobileNo);

    log.info(mobileNo + "-------" + smsString);

    log.info("root url from properties {}", rootUrl);
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    log.info("notificationClientCode from properties {}", notificationClientCode);
    headers.set(X_USER_AGENT_HEADER, notificationClientCode);
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonObject = mapper.createObjectNode();
    jsonObject.putPOJO("phoneNumbers", phoneNumbers);
    jsonObject.put("message", smsString);
    jsonObject.put("confidential", false);
    HttpEntity entity = new HttpEntity<>(jsonObject.toString(), headers);
    log.info("sms api from properties {}", smsApi);
    String url = rootUrl.concat(smsApi);
    ResponseEntity responseEng = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);

    if (responseEng == null) {
      throw new ZoomException("SMS is not sent properly");
    }
    return responseEng.toString();
  }

  @Override
  public Boolean sendSmsV2(String mobileNo, TemplateDTO template) {
    Pair<Boolean, String> validateResponse = validateSendSms(mobileNo, rootUrl);
    if (Boolean.FALSE.equals(validateResponse.getFirst())) {
      log.error("sms cannot be sent due to reason: {}", validateResponse.getSecond());
      return false;
    } else if (template == null) {
      log.error("sms cannot be sent as the template is null");
      return false;
    }

    List<String> phoneNumbers = getPhoneNumbersBasedOnProfile(mobileNo);
    SendSmsRequestDTO requestDto =
        SendSmsRequestDTO.builder()
            .client(notificationClientCodeV2)
            .phoneNumbers(phoneNumbers)
            .template(template)
            .build();
    HttpEntity httpEntity = new HttpEntity<>(requestDto, restClientUtilityService.getHeaders());
    SendSmsResponseDTO response =
        objectMapper.convertValue(
            restClientUtilityService
                .executeRest(rootUrl + smsApiV2, HttpMethod.POST, httpEntity, Object.class)
                .orElseThrow(
                    () ->
                        new ZoomException(
                            "send sms failed for phoneNumbers: {}, templateName: {}",
                            phoneNumbers,
                            template.getName())),
            SendSmsResponseDTO.class);

    if (response.getBulkResponse() == null) {
      log.error(
          "send sms failed for phoneNumbers: {}, templateName: {}, errorCode: {}, exceptionMessage: {}",
          phoneNumbers,
          template.getName(),
          response.getCode(),
          response.getMessage());
      return false;
    }
    log.debug(
        "sms sent successfully to phoneNumbers: {}, templateName: {}, Code: {}, bulkResponse: {}",
        phoneNumbers,
        template.getName(),
        response.getCode(),
        response.getBulkResponse());
    return true;
  }

  private Pair<Boolean, String> validateSendSms(String mobileNo, String rootUrl) {
    log.info("Call to send sms with smsEnable {}", smsEnable);
    if (!smsEnable) {
      log.info("SMS is disabled");
      return Pair.of(false, SMS_DISABLED);
    } else if (StringUtils.isBlank(mobileNo)) {
      return Pair.of(false, INVALID_RECIPIENTS);
    } else if (StringUtils.isBlank(rootUrl)) {
      return Pair.of(false, SMS_SERVER_URL_ABSENT);
    }
    return Pair.of(true, "");
  }

  private List<String> getPhoneNumbersBasedOnProfile(String mobileNo) {
    List<String> phoneNumbers = new ArrayList<>();
    if ("production".equalsIgnoreCase(System.getProperty("spring.profiles.active"))) {
      phoneNumbers.add(mobileNo);
    } else {
      String defaultPhone = zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER, "");
      String[] defaultPhones = defaultPhone.split(",");
      Arrays.stream(defaultPhones)
          .forEach(
              phoneNumber -> {
                log.info("Default phone no is : " + defaultPhone);
                phoneNumbers.add(phoneNumber);
              });
    }
    return phoneNumbers;
  }

  private String getSmsStringBasedOnProfile(String message, String mobileNo) {
    if (!("production".equalsIgnoreCase(System.getProperty("spring.profiles.active")))) {
      return (mobileNo + " - " + message);
    }
    return message;
  }
}
