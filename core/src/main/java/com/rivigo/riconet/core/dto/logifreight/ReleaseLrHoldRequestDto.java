package com.rivigo.riconet.core.dto.logifreight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ReleaseLrHoldRequestDto {

  private ConsignmentHoldRequestDto consignment;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  @Builder
  public static class ConsignmentHoldRequestDto {
    private String shipper_company_code;
    private String consignment_number;
    private final String hold_type = "Eway Bill Hold";
    private final String notes = "release";
  }
}
