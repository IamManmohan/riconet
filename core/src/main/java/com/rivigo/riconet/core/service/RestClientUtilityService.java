package com.rivigo.riconet.core.service;

import com.rivigo.zoom.util.rest.enums.RetryRestRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public interface RestClientUtilityService {

  HttpHeaders getHeaders();

  HttpHeaders getHeaders(String token);

  String buildUrlWithParams(String url, List<Pair<String, String>> params);

  String buildUrlWithParams(String url, MultiValueMap<String, String> params);

  String buildUrlWithParams(String url, Map<String, String> params);

  <T> Optional<T> executeRest(
      String url, HttpMethod httpMethod, HttpEntity entity, Class<T> expectedClass);

  <T> Optional<T> executeRest(
      String url,
      HttpMethod httpMethod,
      HttpEntity entity,
      Class<T> expectedClass,
      Long timeOut,
      Integer retryAttempts);

  <T> Optional<T> executeRest(
      String baseUrl,
      RetryRestRequest retryRestRequest,
      Map<String, String> pathVariableValueMap,
      MultiValueMap<String, String> paramMap,
      Object body,
      HttpHeaders customHeaders,
      Class<T> expectedClass,
      Long timeOut,
      Integer retryAttempts);
}
