package com.rivigo.riconet.core.dto.zoombook;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ZoomBookBfPickupChargesRemarksDTO extends ZoomBookBasicRemarksDTO {

  //used in riconet
  private Long pickupId;
  private Double totalCnWeight;
  private BigDecimal totalCost;
  private Double minimumCharges;
  private Double chargePerKg;
}

