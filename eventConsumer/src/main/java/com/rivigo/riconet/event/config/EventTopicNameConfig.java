package com.rivigo.riconet.event.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** Created by ashfakh on 03/07/19. */
@Configuration
@Getter
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
  private String kairosExpressAppSink;

  @Value("${KAIROS_EXPRESS_APP_SINK_ERROR}")
  private String kairosExpressAppSinkError;

  @Value("${EXPRESS_APP_PICKUP_SINK}")
  private String expressAppPickupSink;

  @Value("${EXPRESS_APP_PICKUP_SINK_ERROR}")
  private String expressAppPickupSinkError;

  @Value("${BF_PICKUP_CHARGES_ACTION_ERROR}")
  private String bfPickupChargesActionError;

  @Value("${CN_ACTION_ERROR}")
  private String cnActionError;

  @Value("${CONSIGNMENT_BLOCK_UNBLOCK_ERROR}")
  private String consignmentBlockUnblockError;

  @Value("${SECONDARY_CN_AUTO_MERGE_ERROR}")
  private String secondaryCnAutoMergeError;

  @Value("${PRIME_EVENT_SINK:PRIME_TO_ZOOM_EVENTS}")
  private String primeEventSink;

  @Value("${PRIME_EVENT_SINK_ERROR}")
  private String primeEventSinkError;

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

  public String kairosExpressAppSink() {
    return kairosExpressAppSink;
  }

  public String kairosExpressAppSinkError() {
    return kairosExpressAppSinkError;
  }

  public String expressAppPickupSink() {
    return expressAppPickupSink;
  }

  public String expressAppPickupSinkError() {
    return expressAppPickupSinkError;
  }

  public String bfPickupChargesActionError() {
    return bfPickupChargesActionError;
  }

  public String cnActionError() {
    return cnActionError;
  }

  public String consignmentBlockUnblockError() {
    return consignmentBlockUnblockError;
  }

  public String secondaryCnAutoMergeError() {
    return secondaryCnAutoMergeError;
  }
}
