package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.impl.UrlShortnerServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Created by sunny on 30/9/22. */
public class URLShortnerServiceTest {

  private static final String shortenerUrl = "http://firebase-root-url.dummy.com";
  private static final String shortenerKey = "abcde";
  private static final String shortenerPrefix = "https://rivigo.short.abc/?link=";

  private static final String shortenerEnabled = "true";

  @InjectMocks private UrlShortnerServiceImpl urlShortnerService;

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

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("key", shortenerKey));
    String paramString = URLEncodedUtils.format(params, "utf-8");
  }

  @Test(expected = HttpClientErrorException.class)
  public void shortenerRetryTest() throws Exception {
    doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .doThrow(HttpClientErrorException.class)
        .when(restTemplate)
        .exchange(
            Mockito.startsWith(shortenerUrl),
            Mockito.eq(HttpMethod.POST),
            Mockito.isA(HttpEntity.class),
            (Class<Object>) any());
    urlShortnerService.shortenUrl("https://rivigo.com/?file=hsjhadjuadbwkjdbwuabduak.pdf");
  }
}
