package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rivigo.riconet.core.service.UrlShortnerService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Created by ashfakh on 21/6/18. */
@Slf4j
@Service
public class UrlShortnerServiceImpl implements UrlShortnerService {

  @Autowired private ObjectMapper objectMapper;

  @Value("${shortener.key}")
  private String shortenerKey;

  @Value("${shortener.url}")
  private String shortenerUrl;

  @Value("${shortener.enabled}")
  private String shortenerEnabled;

  @Override
  public String shortenUrl(String longUrl) {
    if (!Boolean.valueOf(shortenerEnabled)) return longUrl;
    try {

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);

      ObjectNode jsonObject = objectMapper.createObjectNode();
      jsonObject.put("longUrl", longUrl);
      HttpEntity entity = new HttpEntity<>(jsonObject.toString(), headers);

      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("key", shortenerKey));
      String paramString = URLEncodedUtils.format(params, "utf-8");

      ResponseEntity responseEng =
          restTemplate.exchange(
              shortenerUrl + "?" + paramString, HttpMethod.POST, entity, Object.class);

      if (responseEng == null) {
        throw new ZoomException("Url shortener didn't gave proper response");
      }

      log.info(String.valueOf(responseEng));
      Object url = ((LinkedHashMap) responseEng.getBody()).get("id");
      String str = url.toString();
      log.info("Short url from google {}", str);
      return str;
    } catch (Exception e) {
      log.info("Exception occurred while shortening link from google ", e);
      return longUrl;
    }
  }
}
