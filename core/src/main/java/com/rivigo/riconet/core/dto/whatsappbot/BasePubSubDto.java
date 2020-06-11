package com.rivigo.riconet.core.dto.whatsappbot;

import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
public class BasePubSubDto {
  private String id;
  private String eventType;
  private DateTime eventTimestamp;
  private String message;

  @ConstructorProperties({"id", "eventType", "eventTimestamp", "message"})
  public BasePubSubDto(String id, String eventType, DateTime eventTimestamp, String message) {
    this.id = id;
    this.eventType = eventType;
    this.eventTimestamp = eventTimestamp;
    this.message = message;
  }
}
