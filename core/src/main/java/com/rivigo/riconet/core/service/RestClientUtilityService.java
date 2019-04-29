package com.rivigo.riconet.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface RestClientUtilityService {

  HttpHeaders getHeaders();

  HttpHeaders getHeaders(String token);

  String buildUrlWithParams(String url, List<Pair<String, String>> params);

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
}
