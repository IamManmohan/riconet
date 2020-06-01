package com.rivigo.riconet.core.dto.hilti;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DeliveryNotDeliveredDto extends BaseHiltiFieldData {
  private String latLong;
  private String undeliveryReason;
  private String podUndelivered;
  private String rdd;
}
