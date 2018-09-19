package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Created by ashfakh on 19/4/18. */
@Service
@Slf4j
public class TicketingClientService {

  private static final String AUTO_CLOSE_URL_ENDPOINT = "/ticket/autoclose";
  private static final String CLIENT_KEY = "clientKey";
  private static final String ENTITY_ID = "entityId";
  private static final String ENTITY_TYPE = "entityType";
  private static final String EVENT_NAME = "eventName";

  @Value("${zoom.ticketing.url}")
  private String zoomTicketingUrl;

  @Value("${zoom.ticketing.client.key}")
  private String zoomTicketingClientKey;

  private RestTemplate restTemplate = new RestTemplate();

  public void autoCloseTicket(String entityId, String entityType, EventName eventName) {
    log.info(
        "Autoclosing tickets with entityId: {}, entityType: {}, eventName: {}",
        entityId,
        entityType,
        eventName);
    String url = zoomTicketingUrl + AUTO_CLOSE_URL_ENDPOINT;
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.set(ENTITY_ID, entityId);
    queryParams.set(ENTITY_TYPE, entityType);
    queryParams.set(EVENT_NAME, eventName.name());
    makeRequest(url, queryParams);
    log.info("Autoclose tickets called");
  }

  private void makeRequest(String requestUrl, MultiValueMap<String, String> queryParams) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.add(CLIENT_KEY, zoomTicketingClientKey);
      HttpEntity entity = new HttpEntity(headers);
      UriComponentsBuilder builder =
          UriComponentsBuilder.fromHttpUrl(requestUrl).queryParams(queryParams);
      ResponseEntity<JsonNode> responseEntity =
          restTemplate.exchange(
              builder.build().encode().toUri(), HttpMethod.PUT, entity, JsonNode.class);
      log.info("call made successfully, response {}", responseEntity);
      responseEntity.getBody();
    } catch (Exception e) {
      log.error(
          "Unknown exception while trying to make request to ticketing {}, Exception is {}",
          requestUrl,
          e);
      throw new ZoomException("Unknown exception while trying to make request to ticketing");
    }
  }
}
