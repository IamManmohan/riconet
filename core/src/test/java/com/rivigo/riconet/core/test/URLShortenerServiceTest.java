package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.UrlShortnerService;
import java.util.LinkedHashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Created by sunny on 30/9/22. */
@EnableRetry
public class URLShortenerServiceTest extends TesterBase {

  private static final String shortenerUrl = "http://firebase-root-url.dummy.com";
  private static final String shortenerKey = "abcde";
  private static final String shortenerPrefix = "https://rivigo.short.abc/?link=";

  private static final String shortenerEnabled = "true";

  @Autowired private UrlShortnerService urlShortnerService;

  @Mock private RestTemplate restTemplate;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(urlShortnerService, "shortenerKey", shortenerKey);
    ReflectionTestUtils.setField(urlShortnerService, "shortenerUrl", shortenerUrl);
    ReflectionTestUtils.setField(urlShortnerService, "shortenerPrefix", shortenerPrefix);
    ReflectionTestUtils.setField(urlShortnerService, "shortenerEnabled", shortenerEnabled);
    ReflectionTestUtils.setField(urlShortnerService, "objectMapper", new ObjectMapper());
    ReflectionTestUtils.setField(urlShortnerService, "restTemplate", restTemplate);
  }

  @Test
  public void shortenerRetryTest() throws Exception {
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    map.put("shortLink", "ShortURL");
    doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doReturn(ResponseEntity.ok().body(map))
        .when(restTemplate)
        .exchange(
            Mockito.startsWith(shortenerUrl),
            Mockito.eq(HttpMethod.POST),
            Mockito.isA(HttpEntity.class),
            (Class<Object>) any());
    String shortenedUrl =
        urlShortnerService.shortenUrl("https://rivigo.com/?file=hsjhadjuadbwkjdbwuabduak.pdf");
    verify(restTemplate, times(5))
        .exchange(
            Mockito.startsWith(shortenerUrl),
            Mockito.eq(HttpMethod.POST),
            Mockito.isA(HttpEntity.class),
            (Class<Object>) any());
    Assert.assertEquals(shortenedUrl, "ShortURL");
  }
}
