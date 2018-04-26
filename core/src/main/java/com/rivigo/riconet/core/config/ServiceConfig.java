package com.rivigo.riconet.core.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = {
    "com.rivigo.riconet.notification",
    "com.rivigo.riconet.core.service",
    "com.rivigo.riconet.core.config",
    "com.rivigo.riconet.core.test.consumer"
})

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
    return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
        KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<Runnable>(QUEUE_SIZE, true), new ThreadPoolExecutor.CallerRunsPolicy());
  }

  @Bean
  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
