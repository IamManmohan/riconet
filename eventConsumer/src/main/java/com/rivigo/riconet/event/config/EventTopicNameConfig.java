package com.rivigo.riconet.event.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** Created by ashfakh on 03/07/19. */
@Configuration
public class EventTopicNameConfig {
  @Value("${ENRICHED_EVENT_SINK}")
  private String enrichedEventSink;

  @Value("${ENRICHED_EVENT_SINK_ERROR}")
  private String enrichedEventSinkError;

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

  public String enrichedEventSinkErrorTopic() {
    return enrichedEventSinkError;
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
