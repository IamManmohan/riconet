package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rivigo.riconet.core.service.UrlShortnerService;
import java.net.URLEncoder;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Created by ashfakh on 21/6/18. */
@Slf4j
@Service
public class UrlShortnerServiceImpl implements UrlShortnerService {

  @Autowired private ObjectMapper objectMapper;

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${shortener.key}")
  private String shortenerKey;

  @Value("${shortener.url}")
  private String shortenerUrl;

  @Value("${shortener.enabled}")
  private String shortenerEnabled;

  @Value("${shortener.prefix}")
  private String shortenerPrefix;

  @Retryable(
    value = {HttpClientErrorException.class},
    maxAttempts = 5,
    backoff = @Backoff(random = true, delay = 300, maxDelay = 8000)
  )
  @Override
  public String shortenUrl(String longUrl) throws Exception {
    if (!Boolean.parseBoolean(shortenerEnabled)) {
      log.info("skipping url shortening, longUrl: {}", longUrl);
      return longUrl;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    ObjectNode jsonObject = objectMapper.createObjectNode();
    jsonObject.put("longDynamicLink", shortenerPrefix + URLEncoder.encode(longUrl, "UTF-8"));
    jsonObject.putObject("suffix").put("option", "SHORT");
    HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("key", shortenerKey));
    String paramString = URLEncodedUtils.format(params, "utf-8");

    ResponseEntity<Object> responseEng =
        restTemplate.exchange(
            shortenerUrl + "?" + paramString, HttpMethod.POST, entity, Object.class);

    log.info(String.valueOf(responseEng));
    Object url = ((LinkedHashMap<?, ?>) responseEng.getBody()).get("shortLink");
    String str = url.toString();
    log.info("Short url from google {}", str);
    return str;
  }
}
