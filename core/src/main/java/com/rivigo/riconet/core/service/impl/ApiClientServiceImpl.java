package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.cms.constants.ServiceType;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.datastore.DatastoreResponseDto;
import com.rivigo.riconet.core.enums.RequestStatus;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ApiClientServiceImpl implements ApiClientService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private SsoService ssoService;

  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  @Value("${rivigo.sso.password}")
  private String ssoPassword;

  @Value("${billing.base.url}")
  private String billingBaseUrl;

  @Autowired
  @Qualifier("riconetRestTemplate")
  private RestTemplate riconetRestTemplate;

  @Autowired private AccessTokenSsfRedisRepository accessTokenSsfRedisRepository;

  @Override
  public Object parseJsonNode(JsonNode responseJson, TypeReference mapType) {
    String status = responseJson.get(ZoomTicketingConstant.STATUS_KEY).toString();
    log.debug(responseJson.toString());
    log.debug(status);
    if ("\"SUCCESS\"".equals(status)) {
      if (mapType == null) {
        return null;
      }
      try {
        return objectMapper.readValue(
            responseJson.get(ZoomTicketingConstant.RESPONSE_KEY).toString(), mapType);
      } catch (IOException e) {
        log.error(
            "Error while parsing API response,  {} at epoch {} :",
            responseJson.get(ZoomTicketingConstant.RESPONSE_KEY).toString(),
            DateTime.now().getMillis(),
            e);
        throw new ZoomException(
            "Error while parsing API response: errorCode-"
                + DateTime.now().getMillis()
                + " :"
                + e.getMessage());
      }
    }
    String errorMessage = responseJson.get(ZoomTicketingConstant.ERROR_MESSAGE_KEY).toString();
    log.error(
        "API Response Status : {}  Error Message : {} , response : {} ",
        status,
        errorMessage,
        responseJson);
    throw new ZoomException(errorMessage);
  }

  @Override
  public <T> T parseResponseJsonNodeFromDatastore(JsonNode responseJson, JavaType javaType) {

    DatastoreResponseDto response =
        objectMapper.convertValue(responseJson, DatastoreResponseDto.class);

    log.debug(responseJson.toString());
    log.debug(response.getStatus().toString());
    if (response.getStatus().equals(RequestStatus.SUCCESS)) {
      return objectMapper.convertValue(response.getPayload(), javaType);
    }
    log.error(
        "API Response Status : {}  Error Message : {} , response : {} ",
        response.getStatus(),
        response.getErrorMessage(),
        responseJson);
    throw new ZoomException(response.getErrorMessage());
  }

  private HttpHeaders getHeaders(String token, String uri) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (uri.contains(billingBaseUrl)) {
      // The value of Business must be any zoom business type, irrespective of cn business type
      // Identifying business is un-necessary overhead
      headers.add("Business", ServiceType.ZOOM_CORPORATE.name());
    }
    return headers;
  }

  private HttpEntity getHttpEntity(HttpHeaders headers, Object dto, URI uri)
      throws JsonProcessingException {
    if (dto != null) {
      String requestJson = objectMapper.writeValueAsString(dto);
      log.info("Calling API {} for  requestJson {}", uri, requestJson);
      return new HttpEntity<>(dto, headers);
    } else {
      log.info("Calling API {}", uri);
      return new HttpEntity<>(headers);
    }
  }

  @Override
  public JsonNode getEntity(
      Object dto,
      HttpMethod httpMethod,
      String url,
      MultiValueMap<String, String> queryParams,
      String baseUrl)
      throws IOException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
    if (queryParams != null) {
      builder = builder.queryParams(queryParams);
    }
    URI uri = builder.build(true).encode().toUri();
    log.debug("Calling  {} ", uri);
    String token = accessTokenSsfRedisRepository.get(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN);
    if (token == null) {
      log.info("No existing token found. New token is being generated ");
      token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
      accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
    }
    HttpEntity entity = getHttpEntity(getHeaders(token, uri.toString()), dto, uri);

    try {
      log.info(
          "uri: {}, httpMethod: {}, entity: {}",
          uri,
          httpMethod,
          objectMapper.writeValueAsString(entity));
      ResponseEntity<JsonNode> response =
          riconetRestTemplate.exchange(uri.toString(), httpMethod, entity, JsonNode.class);
      log.info("response: {}", response);
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
        log.info("Existing token expired. New token is being generated ");
        token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
        accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
        HttpEntity retryEntity = getHttpEntity(getHeaders(token, uri.toString()), dto, uri);
        try {
          ResponseEntity<JsonNode> response =
              riconetRestTemplate.exchange(uri.toString(), httpMethod, retryEntity, JsonNode.class);
          return response.getBody();
        } catch (HttpStatusCodeException e2) {
          log.error("Invalid response from API  while calling {}", DateTime.now(), e2);
          throw new ZoomException("Invalid response from API " + e2.getMessage());
        }
      }
      log.error("Invalid response from API  while calling {}", DateTime.now(), e);
      throw new ZoomException("Invalid response from API " + e.getMessage());
    }
  }
}
