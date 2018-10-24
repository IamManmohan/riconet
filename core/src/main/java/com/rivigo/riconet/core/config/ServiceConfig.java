package com.rivigo.riconet.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    "com.rivigo.riconet.core.test.consumer",
    "com.rivigo.oauth2.resource.service",
    "com.rivigo.riconet.ruleengine",
    "com.rivigo.riconet.core.config",
    "com.rivigo.riconet.core.test.consumer",
    "com.rivigo.riconet.event.service"
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
    mapper.registerModule(new GeoJsonModule());
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

  @Bean(name = {"myProperties"})
  public static PropertiesFactoryBean mapper(
      @Value("${login.profiles.active:staging}") String classPath) {
    PropertiesFactoryBean bean = new PropertiesFactoryBean();
    bean.setLocation(new ClassPathResource(classPath + "/authresource.properties"));
    return bean;
  }
}
