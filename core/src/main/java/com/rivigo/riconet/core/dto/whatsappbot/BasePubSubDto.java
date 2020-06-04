package com.rivigo.riconet.core.dto.whatsappbot;

import java.beans.ConstructorProperties;
import org.joda.time.DateTime;

public class BasePubSubDto {
  private String id;
  private String eventType;
  private DateTime eventTimestamp;
  private String message;

  public String getId() {
    return this.id;
  }

  public String getEventType() {
    return this.eventType;
  }

  public DateTime getEventTimestamp() {
    return this.eventTimestamp;
  }

  public String getMessage() {
    return this.message;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public void setEventTimestamp(DateTime eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @ConstructorProperties({"id", "eventType", "eventTimestamp", "message"})
  public BasePubSubDto(String id, String eventType, DateTime eventTimestamp, String message) {
    this.id = id;
    this.eventType = eventType;
    this.eventTimestamp = eventTimestamp;
    this.message = message;
  }

  public BasePubSubDto() {}
}
