package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  private RestTemplate restTemplate = new RestTemplate();

  @Autowired private AccessTokenSsfRedisRepository accessTokenSsfRedisRepository;

  @Autowired private SsoService ssoService;

  @Value("${zoom.ticketing.url}")
  private String zoomTicketingUrl;

  @Value("${zoom.ticketing.client.key}")
  private String zoomTicketingClientKey;

  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  @Value("${rivigo.sso.password}")
  private String ssoPassword;

  public void autoCloseTicket(String entityId, String entityType, String condition) {
    log.info(
        "Autoclosing tickets with entityId: {}, entityType: {}, eventName: {}",
        entityId,
        entityType,
        condition);
    String url = zoomTicketingUrl + AUTO_CLOSE_URL_ENDPOINT;
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.set(ENTITY_ID, entityId);
    queryParams.set(ENTITY_TYPE, entityType);
    queryParams.set(EVENT_NAME, condition);
    makeRequestWithMediaTypeUrlEncoded(url, queryParams);
  }
  /**
   * AutoClosing a Ticket with metadata
   *
   * @param entityId contains the value of the entity
   * @param entityType is the {@link TicketEntityType}
   * @param condition is the name of event triggered
   * @param metadata contains the more information required.
   */
  public void autoCloseTicketWithMetaData(
      String entityId, String entityType, String condition, Map<String, String> metadata) {
    log.info(
        "autoCloseTicketWithMetaData with entityId: {}, entityType: {}, eventName: {}, metadata: {}",
        entityId,
        entityType,
        condition,
        metadata);
    String url = zoomTicketingUrl + UrlConstant.AUTO_CLOSE_WITH_METADATA_URL_ENDPOINT;
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.set(ENTITY_ID, entityId);
    queryParams.set(ENTITY_TYPE, entityType);
    queryParams.set(EVENT_NAME, condition);
    makeRequestWithMediaTypeApplicationJson(url, queryParams, metadata);
  }

  private HttpEntity getHttpEntity(HttpHeaders headers, Object dto) {
    if (dto != null) {
      return new HttpEntity<>(dto, headers);
    } else {
      return new HttpEntity<>(headers);
    }
  }

  private HttpHeaders getHeadersWithMediaTypeUrlEncoded(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add(CLIENT_KEY, zoomTicketingClientKey);
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }

  private HttpHeaders getHeadersWithMediaTypeApplicationJson(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add(CLIENT_KEY, zoomTicketingClientKey);
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private String getToken() {
    String token = accessTokenSsfRedisRepository.get(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN);
    if (token == null) {
      log.info("No existing token found. New token is being generated ");
      token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
      accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
    }
    return token;
  }

  private void makeRequestWithMediaTypeUrlEncoded(
      String requestUrl, MultiValueMap<String, String> queryParams) {
    String token = getToken();
    try {
      HttpEntity entity = getHttpEntity(getHeadersWithMediaTypeUrlEncoded(token), null);
      makeRequest(requestUrl, queryParams, entity);
    } catch (Exception e) {
      throw new ZoomException(e);
    }
  }

  private void makeRequestWithMediaTypeApplicationJson(
      String requestUrl, MultiValueMap<String, String> queryParams, Object dto) {
    String token = getToken();
    try {
      HttpEntity entity = getHttpEntity(getHeadersWithMediaTypeApplicationJson(token), dto);
      makeRequest(requestUrl, queryParams, entity);
    } catch (Exception e) {
      throw new ZoomException(e);
    }
  }

  private void makeRequest(
      String requestUrl, MultiValueMap<String, String> queryParams, HttpEntity entity) {
    try {
      UriComponentsBuilder builder =
          UriComponentsBuilder.fromHttpUrl(requestUrl).queryParams(queryParams);
      ResponseEntity<JsonNode> responseEntity =
          restTemplate.exchange(
              builder.build().encode().toUri(), HttpMethod.PUT, entity, JsonNode.class);
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
