package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rivigo.riconet.core.service.PushNotificationService;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

  // @Value("${firebase.url}")
  private String firebaseUrl = "https://fcm.googleapis.com/fcm/send";

  //    @Value("${firebase.server.key}")
  private String firebaseServerKey = "AIzaSyD9E1NeCzE_NpCMA6v4zbhhei64yVxiixw";

  @Autowired private RestTemplate riconetRestTemplate;

  @Autowired private ObjectMapper objectMapper;

  private HttpHeaders getHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add(HttpHeaders.AUTHORIZATION, "key=" + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpEntity getHttpEntity(HttpHeaders headers, Object dto, URI uri)
      throws JsonProcessingException {
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
  public void send(String message, String firebaseToken) throws IOException {

    JSONObject body = new JSONObject();
    body.put("to", firebaseToken);
    body.put("priority", "high");

    JSONObject notification = new JSONObject();
    notification.put("title", "Unloading In Loading");
    notification.put("body", "hey! unloading in loading is happening!");

//    JSONObject data = new JSONObject();
//    data.put("Key-1", "JSA Data 1");
//    data.put("Key-2", "JSA Data 2");

    body.put("notification", notification);
   // body.put("data", data);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(firebaseUrl);
    URI uri = builder.build().encode().toUri();
    HttpEntity entity = getHttpEntity(getHeaders(firebaseServerKey), body, uri);
    ResponseEntity<JSONObject> firebaseResponse =
        riconetRestTemplate.exchange(uri.toString(), HttpMethod.POST, entity, JSONObject.class);
  }
}
