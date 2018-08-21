package com.rivigo.riconet.core.service;

import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface RestClientUtilityService {

  HttpHeaders getHeaders();

  HttpHeaders getHeaders(String token);

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
