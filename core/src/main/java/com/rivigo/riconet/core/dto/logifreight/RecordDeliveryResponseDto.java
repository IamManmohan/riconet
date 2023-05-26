package com.rivigo.riconet.core.dto.logifreight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class RecordDeliveryResponseDto {
  private LrDeliveryResponseDto lr;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class LrDeliveryResponseDto {
    private Long id;
    private String number;
    private String delivery_verification_code;
    private String delivery_feedback_required;
    private String can_deliver_to_pay_without_payment;
  }
}
