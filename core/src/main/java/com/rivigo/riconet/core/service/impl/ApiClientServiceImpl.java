package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SsoService ssoService;

  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  @Value("${rivigo.sso.password}")
  private String ssoPassword;

  @Autowired
  private AccessTokenSsfRedisRepository accessTokenSsfRedisRepository;

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
        return objectMapper.readValue(responseJson.get(ZoomTicketingConstant.RESPONSE_KEY).toString(), mapType);
      } catch (IOException e) {
        log.error("Error while parsing ticketing response,  {} at epoch {} :",
            responseJson.get(ZoomTicketingConstant.RESPONSE_KEY).toString(), DateTime.now().getMillis(), e);
        throw new ZoomException(
            "Error while parsing ticketing response: errorCode-" + DateTime.now().getMillis() + " :"
                + e.getMessage());
      }
    }
    throw new ZoomException(responseJson.get(ZoomTicketingConstant.ERROR_MESSAGE_KEY).toString());
  }

  private HttpHeaders getHeaders(String token){
    HttpHeaders headers=new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpEntity getHttpEntity(HttpHeaders headers, Object dto)
      throws JsonProcessingException {
    if (dto != null) {
      String requestJson = objectMapper.writeValueAsString(dto);
      return new HttpEntity<Object>(requestJson, headers);
    } else {
      return new HttpEntity<>(headers);
    }
  }

  @Override
  public JsonNode getEntity(Object dto, HttpMethod httpMethod, String url,
      MultiValueMap<String, String> queryParams, String baseUrl) throws IOException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
    if (queryParams != null) {
      builder = builder.queryParams(queryParams);
    }
    log.debug("Calling  {} ", builder.build().encode().toUri());
    String token = accessTokenSsfRedisRepository.get(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN);
    if (token == null) {
      token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
    }
    HttpEntity<Object> entity=getHttpEntity(getHeaders(token),dto);
    RestTemplate restTemplate = new RestTemplate();

    try {
      ResponseEntity<JsonNode> response = restTemplate
          .exchange(builder.build().encode().toUri(), httpMethod,
              entity, JsonNode.class);
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
        token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
        accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
        HttpEntity<Object> retryEntity=getHttpEntity(getHeaders(token),dto);
        try {
          ResponseEntity<JsonNode> response = restTemplate
              .exchange(builder.build().encode().toUri(), httpMethod,
                  retryEntity, JsonNode.class);
          return response.getBody();
        } catch (HttpStatusCodeException e2) {
          log.error("Invalid response from ticketing  while calling {}", DateTime.now(), e2);
          throw new ZoomException("Invalid response from ticketing " + e2.getMessage());
        }
      }
      log.error("Invalid response from ticketing  while calling {}", DateTime.now(), e);
      throw new ZoomException("Invalid response from ticketing " + e.getMessage());
    }
  }

}

