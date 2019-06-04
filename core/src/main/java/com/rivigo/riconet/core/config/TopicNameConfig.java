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

  @Value("${FINANCE_EVENT_SINK}")
  private String financeEventSink;

  @Value("${FINANCE_EVENT_SINK_ERROR}")
  private String financeEventSinkError;

  @Value("${WMS_EVENT_SINK}")
  private String wmsEventSink;

  @Value("${WMS_EVENT_SINK_ERROR}")
  private String wmsEventSinkError;

  @Value("${KAIROS_EXPRESS_APP_SINK}")
  private String kairosExpressAppSinkTopic;

  @Value("${EXPRESS_APP_PICKUP_SINK}")
  private String expressAppPickupSink;

  @Value("${EXPRESS_APP_PICKUP_SINK_ERROR}")
  private String expressAppPickupSinkError;

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

  public String kairosExpressAppSinkTopic() {
    return kairosExpressAppSinkTopic;
  }

  public String expressAppPickupSink() {
    return expressAppPickupSink;
  }

  public String expressAppPickupSinkError() {
    return expressAppPickupSinkError;
  }
}
