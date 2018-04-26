package com.rivigo.riconet.core.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
@SuppressWarnings("unused")
@Component
public class TopicNameConfig {

  @Value("${RAW_EVENT_SINK}")
  private String RAW_EVENT_SINK;

  @Value("${ENRICHED_EVENT_SINK}")
  private String ENRICHED_EVENT_SINK;

  @Value("${ENRICHED_EVENT_SINK_ERROR}")
  private String ENRICHED_EVENT_SINK_ERROR;

  @Value("${SMS_SINK}")
  private String SMS_SINK;

  @Value("${SMS_SINK_ERROR}")
  private String SMS_SINK_ERROR;


  public String ENRICHED_EVENT_SINK_TOPIC() {
    return ENRICHED_EVENT_SINK;
  }

  public String ENRICHED_EVENT_SINK_ERROR_TOPIC() {
    return ENRICHED_EVENT_SINK_ERROR;
  }

  public String RAW_EVENT_SINK() {
    return RAW_EVENT_SINK;
  }

  public String SMS_SINK() {
    return SMS_SINK;
  }

  public String SMS_SINK_ERROR() {
    return SMS_SINK_ERROR;
  }

}