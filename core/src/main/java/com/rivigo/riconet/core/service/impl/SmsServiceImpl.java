package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.predicates.EnvironmentPredicate.isActiveSpringProfileProduction;
import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rivigo.riconet.core.dto.TemplateV2DTO;
import com.rivigo.riconet.core.dto.platformteam.SendSmsV2RequestDTO;
import com.rivigo.riconet.core.dto.platformteam.SendSmsV2ResponseDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Service;

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
  /**
   * use `sendSmsV2` for sending any new messages, as this function supports sending sms only via
   * message.
   */
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
    HttpHeaders headers = restClientUtilityService.getHeaders();
    log.info("notificationClientCode from properties {}", notificationClientCode);
    headers.set(X_USER_AGENT_HEADER, notificationClientCode);

    ObjectNode jsonObject = objectMapper.createObjectNode();
    jsonObject.putPOJO("phoneNumbers", phoneNumbers);
    jsonObject.put("message", smsString);
    jsonObject.put("confidential", false);

    HttpEntity entity = new HttpEntity<>(jsonObject.toString(), headers);
    log.info("sms api from properties {}", smsApi);
    String url = rootUrl.concat(smsApi);

    Object response =
        restClientUtilityService
            .executeRest(url, HttpMethod.POST, entity, Object.class)
            .orElseThrow(() -> new ZoomException("SMS is not sent properly"));
    return response.toString();
  }

  @Override
  /**
   * this is the new send sms flow implementation and supports sending sms via template as well as
   * via message.
   */
  public Boolean sendSmsV2(String mobileNo, TemplateV2DTO templateV2) {
    Pair<Boolean, String> validateResponse = validateSendSms(mobileNo, rootUrl);
    if (Boolean.FALSE.equals(validateResponse.getFirst())) {
      log.error("sms cannot be sent due to reason: {}", validateResponse.getSecond());
      return false;
    } else if (templateV2 == null) {
      log.error("sms cannot be sent as the templateV2 is null");
      return false;
    }

    List<String> phoneNumbers = getPhoneNumbersBasedOnProfile(mobileNo);
    SendSmsV2RequestDTO requestDto =
        SendSmsV2RequestDTO.builder()
            .client(notificationClientCodeV2)
            .phoneNumbers(phoneNumbers)
            .template(templateV2)
            .build();
    HttpEntity httpEntity = new HttpEntity<>(requestDto, restClientUtilityService.getHeaders());
    SendSmsV2ResponseDTO response =
        objectMapper.convertValue(
            restClientUtilityService
                .executeRest(rootUrl + smsApiV2, HttpMethod.POST, httpEntity, Object.class)
                .orElseThrow(
                    () ->
                        new ZoomException(
                            "send sms failed for phoneNumbers: {}, templateName: {}",
                            phoneNumbers,
                            templateV2.getName())),
            SendSmsV2ResponseDTO.class);

    if (response.getBulkResponse() == null) {
      log.error(
          "send sms failed for phoneNumbers: {}, templateName: {}, errorCode: {}, exceptionMessage: {}",
          phoneNumbers,
          templateV2.getName(),
          response.getCode(),
          response.getMessage());
      return false;
    }
    log.debug(
        "sms sent successfully to phoneNumbers: {}, templateName: {}, Code: {}, bulkResponse: {}",
        phoneNumbers,
        templateV2.getName(),
        response.getCode(),
        response.getBulkResponse());
    return true;
  }

  private Pair<Boolean, String> validateSendSms(String mobileNo, String rootUrl) {
    log.info("Call to send sms with smsEnable {}", smsEnable);
    if (!Boolean.TRUE.equals(smsEnable)) {
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
    if (isActiveSpringProfileProduction().test(System.getProperty(ACTIVE_PROFILES_PROPERTY_NAME))) {
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
    if (isActiveSpringProfileProduction()
        .negate()
        .test(System.getProperty(ACTIVE_PROFILES_PROPERTY_NAME))) {
      return String.format("%s - %s", mobileNo, message);
    }
    message = message.replaceAll("[^\\p{ASCII}]", "");
    return message;
  }
}
