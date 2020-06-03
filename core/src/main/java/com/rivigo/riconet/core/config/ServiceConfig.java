package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@ComponentScan(
  basePackages = {
    "com.rivigo.riconet.notification",
    "com.rivigo.riconet.event",
    "com.rivigo.riconet.core.service",
    "com.rivigo.riconet.core.consumer",
    "com.rivigo.riconet.core.test.consumer",
    "com.rivigo.oauth2.resource.service",
    "com.rivigo.riconet.ruleengine",
    "com.rivigo.riconet.core.config",
    "com.rivigo.riconet.core.test.consumer",
    "com.rivigo.riconet.event.service",
    "com.rivigo.transaction.manager.client.service",
    "com.rivigo.zoom.util.rest"
  }
)
@Slf4j
public class ServiceConfig {

  private static final int CORE_POOL_SIZE = 10;
  private static final int MAX_POOL_SIZE = 20;
  private static final int QUEUE_SIZE = 10;
  private static final int KEEP_ALIVE_TIME = 5000;

  @Bean
  ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setThreadNamePrefix("zoom_scheduler_");
    taskScheduler.setPoolSize(5);
    taskScheduler.setRemoveOnCancelPolicy(true);
    taskScheduler.initialize();

    return taskScheduler;
  }

  @Bean
  @Primary
  public ExecutorService getExecutorService() {
    return new ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<Runnable>(QUEUE_SIZE, true),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  @Bean
  ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    return mapper;
  }

  @Bean
  @Qualifier("riconetRestTemplate")
  RestTemplate riconetRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }

  /** Copied from com.rivigo.oauth2.resource.config.OAuth2ResourceConfig ** START ** */
  @Bean(name = "myProperties")
  public PropertiesFactoryBean mapper(
      final @Value("${login.profiles.active:staging}") String classPath) {
    PropertiesFactoryBean bean = new PropertiesFactoryBean();
    bean.setLocation(new ClassPathResource(classPath + "/authresource.properties"));
    return bean;
  }

  @Bean(name = "ssoServiceObjectMapper")
  public ObjectMapper ssoServiceObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  @Bean(name = "ssoServiceRestTemplate")
  public RestTemplate ssoServiceRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(Collections.singletonList(new RequestInterceptor()));
    return restTemplate;
  }

  private class RequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
        HttpRequest httpRequest,
        byte[] bytes,
        ClientHttpRequestExecution clientHttpRequestExecution)
        throws IOException {
      log.info("SsoService: Making {} call to {}", httpRequest.getMethod(), httpRequest.getURI());
      ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
      log.info("SsoService: Received response from sso for request to {}", httpRequest.getURI());
      return response;
    }
  }

  /** Copied from com.rivigo.oauth2.resource.config.OAuth2ResourceConfig ** END ** */
}
