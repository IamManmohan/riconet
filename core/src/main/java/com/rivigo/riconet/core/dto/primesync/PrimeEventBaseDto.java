package com.rivigo.riconet.core.dto.primesync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PrimeEventBaseDto {
  private String eventType;
  private DateTime eventTimestamp;
  private String message;
}
