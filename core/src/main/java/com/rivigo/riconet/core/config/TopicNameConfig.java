package com.rivigo.riconet.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@SuppressWarnings("unused")
public class TopicNameConfig {

  @Value("${RAW_EVENT_SINK}")
  private String rawEventSink;

  @Value("${ENRICHED_EVENT_SINK}")
  private String enrichedEventSink;

  @Value("${RAW_EVENT_SINK_ERROR}")
  private String rawEventSinkError;

  @Value("${ENRICHED_EVENT_SINK_ERROR}")
  private String enrichedEventSinkError;

  @Value("${SMS_SINK}")
  private String smsSink;

  @Value("${SMS_SINK_ERROR}")
  private String smsSinkError;

  public String enrichedEventSinkTopic() {
    return enrichedEventSink;
  }

  public String rawEventSinkTopic() {
    return rawEventSink;
  }

  public String enrichedEventSinkErrorTopic() {
    return enrichedEventSinkError;
  }

  public String rawEventSinkErrorTopic() {
    return rawEventSinkError;
  }

  public String smsSink() {
    return smsSink;
  }

  public String smsSinkError() {
    return smsSinkError;
  }
}
