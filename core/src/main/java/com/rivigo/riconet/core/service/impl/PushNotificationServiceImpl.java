package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.PushNotificationConstant.PRIORITY;
import static com.rivigo.riconet.core.constants.PushNotificationConstant.TO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.zoom.common.enums.ApplicationId;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Created by ashfakh on 21/09/18. */
@Service
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

  @Value("${firebase.url}")
  private String firebaseUrl = "https://fcm.googleapis.com/fcm/send";

  @Value("${firebase.server.key}")
  private String firebaseServerKey = "AIzaSyD9E1NeCzE_NpCMA6v4zbhhei64yVxiixw";

  private static final String EXPRESS_APP_SERVER_KEY_STAGING =
      "AAAANqWYBzY:APA91bEpPEGrFH4SXebidxquJaEHom8fzz4L8WM05rAPO0Q9S-URTHV3fhO0tdvQllcuvcBukoInWlfeSIua7ASmWG445Wa7lstSckr-swF3HOZN_99Nm7BV-ExQ15EQJ51cC6D3V2Rr";

  private static final String EXPRESS_APP_SERVER_KEY_PROD =
      "AAAAmP-QmNg:APA91bFHjJ-pclgU2_5V7DAwH9sO_VY_sLVwbayH2MzQ-qqiwIbOR1SWcW1vSBAoB_6a_ovygokWzvsENmRs-9IIdZrFIo9JS1wyIkbyG2nzQV3QWhxcU7OUpHag1VMnwIeC698hd44y";

  @Autowired private ObjectMapper objectMapper;

  private HttpHeaders getHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "key=" + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpEntity getHttpEntity(HttpHeaders headers, Object dto, URI uri) {
    if (dto != null) {
      log.info("Calling API {} for  requestJson {}", uri, dto.toString());
      objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
      return new HttpEntity<>(dto.toString(), headers);
    } else {
      log.info("Calling API {}", uri);
      return new HttpEntity<>(headers);
    }
  }

  @Override
  public void send(
      JSONObject jsonObject, String firebaseToken, String priority, ApplicationId applicationId) {

    if (firebaseToken == null) {
      return;
    }
    Boolean isProd = true;
    if (!"production"
        .equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
      isProd = false;
    }
    // TODO : see why autowired restemplate is giving bad request
    RestTemplate restTemplate = new RestTemplate();
    jsonObject.put(PRIORITY, priority);
    jsonObject.put(TO, firebaseToken);
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(firebaseUrl);
    URI uri = builder.build().encode().toUri();
    String token;
    if (ApplicationId.retail_app.equals(applicationId)) {
      if (isProd) token = EXPRESS_APP_SERVER_KEY_PROD;
      else token = EXPRESS_APP_SERVER_KEY_STAGING;
    } else token = firebaseServerKey;
    log.debug("the notif I am sending is  {} and token is :{}", jsonObject, token);
    HttpEntity entity = getHttpEntity(getHeaders(token), jsonObject, uri);
    ResponseEntity<JSONObject> response =
        restTemplate.exchange(firebaseUrl, HttpMethod.POST, entity, JSONObject.class);
    log.info("Response is {}", response);
  }
}
