package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.RestUtilConstants.DEFAULT_RETRY_ATTEMPTS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.DEFAULT_TIMEOUT_MILLIS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.MAX_RETRY_ATTEMPTS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.TOKEN_PREFIX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RestClientUtilityServiceImpl implements RestClientUtilityService {

  private RestTemplate restTemplate = new RestTemplate();
  @Autowired private ExecutorService executorService;
  @Autowired private ObjectMapper objectMapper;

  @Override
  public HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  @Override
  public HttpHeaders getHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token);
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  @Override
  public <T> Optional<T> executeRest(
      String url, HttpMethod httpMethod, HttpEntity entity, Class<T> expectedClass) {
    return executeRest(
        url, httpMethod, entity, expectedClass, DEFAULT_TIMEOUT_MILLIS, DEFAULT_RETRY_ATTEMPTS);
  }

  @Override
  public <T> Optional<T> executeRest(
      String url,
      HttpMethod httpMethod,
      HttpEntity entity,
      Class<T> expectedClass,
      Long timeoutMillis,
      Integer retryAttempts) {
    Optional<T> response = Optional.empty();
    Integer maxRetryAttempts =
        null == retryAttempts
            ? DEFAULT_RETRY_ATTEMPTS
            : Math.min(retryAttempts, MAX_RETRY_ATTEMPTS);
    Integer retryCount = 0;
    while (retryCount <= maxRetryAttempts) {
      try {
        log.info("retryCount {}", retryCount);
        response =
            Optional.ofNullable(
                executeRestWithTimeout(url, httpMethod, entity, expectedClass, timeoutMillis));
        break;
      } catch (TimeoutException | ExecutionException | InterruptedException e) {
        log.error("API call failed, retryCount : {}", retryCount, e);
        if (e.getCause() instanceof ZoomException) {
          throw new ZoomException(e.getCause().getMessage());
        }
      }
      retryCount++;
    }
    return response;
  }

  private <T> T executeRestWithTimeout(
      String url,
      HttpMethod httpMethod,
      HttpEntity entity,
      Class<T> expectedClass,
      Long timeoutMillis)
      throws TimeoutException, ExecutionException, InterruptedException {
    try {
      log.info(
          "request: url: {}, httpMethod: {}, entity: {}, timeoutMillis: {}",
          url,
          httpMethod,
          objectMapper.writeValueAsString(entity),
          timeoutMillis);
    } catch (JsonProcessingException e) {
      log.warn("Could not log request: ", e);
    }
    final Future<T> f =
        executorService.submit(() -> executeRestApi(url, httpMethod, entity, expectedClass));
    T response =
        f.get(
            timeoutMillis == null ? DEFAULT_TIMEOUT_MILLIS : timeoutMillis, TimeUnit.MILLISECONDS);
    try {
      log.info(
          "request: url: {}, httpMethod: {}, entity: {}, timeoutMillis: {}, response: {}",
          url,
          httpMethod,
          objectMapper.writeValueAsString(entity),
          timeoutMillis,
          objectMapper.writeValueAsString(response));
    } catch (JsonProcessingException e) {
      log.warn("Could not log request-response: ", e);
    }
    return response;
  }

  private <T> T executeRestApi(
      String url, HttpMethod httpMethod, HttpEntity entity, Class<T> clazz) {
    try {
      ResponseEntity<T> responseEntity = restTemplate.exchange(url, httpMethod, entity, clazz);
      if (responseEntity.getStatusCode().is5xxServerError()) {
        throw new ZoomException(
            "Unable to connect to server: {%s}", responseEntity.getStatusCode());
      }
      if (responseEntity.getStatusCode().is4xxClientError()) {
        throw new ZoomException(
            "Invalid request received:{%s}. Response: {%s}", entity, responseEntity.getStatusCode());
      }

      return responseEntity.getBody();

    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }
}
