package com.rivigo.riconet.event.dto.whatsappbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasePubSubDto {
  private String id;
  private String eventType;
  private DateTime eventTimestamp;
  private String message;
}
