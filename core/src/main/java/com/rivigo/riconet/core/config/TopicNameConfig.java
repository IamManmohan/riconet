package com.rivigo.riconet.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@SuppressWarnings("unused")
public class TopicNameConfig {

  @Value("${ENRICHED_EVENT_SINK}")
  private String enrichedEventSink;

  @Value("${ENRICHED_EVENT_SINK_ERROR}")
  private String enrichedEventSinkError;

  @Value("${SMS_SINK}")
  private String smsSink;

  @Value("${SMS_SINK_ERROR}")
  private String smsSinkError;

  @Value("${FINANCE_EVENT_SINK}")
  private String financeEventSink;

  @Value("${FINANCE_EVENT_SINK_ERROR}")
  private String financeEventSinkError;

  @Value("${WMS_EVENT_SINK}")
  private String wmsEventSink;

  @Value("${WMS_EVENT_SINK_ERROR}")
  private String wmsEventSinkError;

  public String enrichedEventSinkTopic() {
    return enrichedEventSink;
  }

  public String enrichedEventSinkErrorTopic() {
    return enrichedEventSinkError;
  }

  public String smsSink() {
    return smsSink;
  }

  public String smsSinkError() {
    return smsSinkError;
  }

  public String financeEventSink() {
    return financeEventSink;
  }

  public String financeEventSinkError() {
    return financeEventSinkError;
  }

  public String wmsEventSink() {
    return wmsEventSink;
  }

  public String wmsEventSinkError() {
    return wmsEventSinkError;
  }
}
