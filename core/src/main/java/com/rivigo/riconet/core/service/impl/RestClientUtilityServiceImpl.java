package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.RestUtilConstants.DEFAULT_RETRY_ATTEMPTS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.DEFAULT_TIMEOUT_MILLIS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.MAX_RETRY_ATTEMPTS;
import static com.rivigo.riconet.core.constants.RestUtilConstants.TOKEN_PREFIX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Qualifier("defaultRestClientUtilityServiceImpl")
public class RestClientUtilityServiceImpl implements RestClientUtilityService {

  protected RestTemplate restTemplate = new RestTemplate();
  @Autowired protected ExecutorService executorService;
  @Autowired protected ObjectMapper objectMapper;

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

  /**
   * RestTemplate.exchange internally encodes the url to UTF-8. Do not encode here.
   *
   * @param url
   * @param params
   * @return
   */
  @Override
  public String buildUrlWithParams(String url, List<Pair<String, String>> params) {
    if (CollectionUtils.isEmpty(params)) return url;
    return url
        + params
            .stream()
            .map(p -> p.getFirst() + "=" + p.getSecond())
            .reduce((p1, p2) -> p1 + "&" + p2)
            .map(s -> "?" + s)
            .orElse("");
  }

  @Override
  public String buildUrlWithParams(String url, MultiValueMap<String, String> params) {
    if (CollectionUtils.isEmpty(params)) return url;
    return url
        + params
            .entrySet()
            .stream()
            .flatMap(e -> e.getValue().stream().map(v -> Pair.of(e.getKey(), v)))
            .map(p -> p.getFirst() + "=" + p.getSecond())
            .reduce((p1, p2) -> p1 + "&" + p2)
            .map(s -> "?" + s)
            .orElse("");
  }

  @Override
  public String buildUrlWithParams(String url, Map<String, String> params) {
    if (CollectionUtils.isEmpty(params)) return url;
    return url
        + params
            .entrySet()
            .stream()
            .map(p -> p.getKey() + "=" + p.getValue())
            .reduce((p1, p2) -> p1 + "&" + p2)
            .map(s -> "?" + s)
            .orElse("");
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

  public <T> T executeRestApi(
      String url, HttpMethod httpMethod, HttpEntity entity, Class<T> clazz) {
    try {
      ResponseEntity<T> responseEntity = restTemplate.exchange(url, httpMethod, entity, clazz);
      if (responseEntity.getStatusCode().is5xxServerError()
          || responseEntity.getStatusCode().is4xxClientError()) {
        throw new ZoomException(
            "Unable to connect to server: {%s}", responseEntity.getStatusCode());
      }
      return responseEntity.getBody();

    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }
}
