package com.rivigo.riconet.core.dto.hilti;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DeliveryNotDeliveredDto extends BaseHiltiFieldData {
  private String latLong;
  private String undeliveryReason;
  private String podUndelivered;
  /**
   * Revised edd sent to client. This value is taken from consignment schedule
   * (scheduled_arrival_time column) for TO_PINCODE location.
   */
  private String rdd;
}
