package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public interface ApiClientService {

  Object parseJsonNode(JsonNode responseJson, TypeReference mapType);

  JsonNode getEntity(
      Object dto,
      HttpMethod httpMethod,
      String url,
      MultiValueMap<String, String> queryParams,
      String baseUrl)
      throws IOException;
}
