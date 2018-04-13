package com.rivigo.riconet.core.service;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.zoom.exceptions.ZoomException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SmsService {

  public final static String X_USER_AGENT_HEADER = "X-User-Agent";
  private static final String SMS_DISABLED = "sending sms is disabled";
  private static final String SMS_SERVER_URL_ABSENT = "sms server url is absent";
  private static final String SMS_STRING_ABSENT = "sms string is absent";
  private static final String INVALID_RECIPIENTS = "invalid recipients";
  @Value("${notification.root.url}")
  public String rootUrl;
  @Value("${notification.sms.api}")
  public String smsApi;
  @Value("${notification.sms.enable}")
  public Boolean smsEnable;
  @Value("${notification.client.code}")
  public String notificationClientCode;
  @Autowired
  public ZoomPropertyService zoomPropertyService;

  public String sendSms(String mobileNo, String message) throws JSONException {

    log.info("Call to send sms");
    if (!smsEnable) {
      log.info("SMS is disabled");
      return SMS_DISABLED;
    }
    if (message == null) {
      return SMS_STRING_ABSENT;
    }
    if (mobileNo == null) {
      return INVALID_RECIPIENTS;
    }
    List<String> phoneNumbers = new ArrayList<>();
    String smsString = message;
    if ("production".equalsIgnoreCase(System.getProperty("spring.profiles.active"))) {
      phoneNumbers.add(mobileNo);
    } else {
      String defaultPhone = zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER);
      log.info("Default phone no is : " + defaultPhone);
      defaultPhone = "8553959140";
      phoneNumbers.add(defaultPhone);
      smsString = mobileNo + " - " + smsString;
    }


    log.info(mobileNo + "-------" + smsString);

    if(!StringUtils.isNullOrEmpty(rootUrl)) {
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(X_USER_AGENT_HEADER, notificationClientCode);
      JSONObject jsonObject = new JSONObject();

      jsonObject.put("phoneNumbers", phoneNumbers);
      jsonObject.put("message", smsString);
      jsonObject.put("confidential", true);
      HttpEntity entity = new HttpEntity<>(jsonObject.toString(), headers);
      String url = rootUrl.concat(smsApi);
      ResponseEntity responseEng = restTemplate.exchange(url,
          HttpMethod.POST, entity, Object.class);

      if (responseEng == null) {
        throw new ZoomException("SMS is not sent properly");
      }
      return responseEng.toString();
    }
    return SMS_SERVER_URL_ABSENT;
  }
}
