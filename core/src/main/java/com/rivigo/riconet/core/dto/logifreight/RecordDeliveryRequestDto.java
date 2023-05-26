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
public class RecordDeliveryRequestDto {
  private LrDeliveryRequestDto lr;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  @Builder
  public static class LrDeliveryRequestDto {
    private String number;
    private String damaged_packages;
    private String delivered_at_location;
    private String delivered_at;
    private String delivered_packages;
    private String delivered_to_contact_name;
    private String delivered_to_contact_phone_number;
    private final String delivery_exception = "Y";
    private String temperature;
    private String delivery_lat;
    private String delivery_lng;
    private String delivery_notes;
    private String reason_for_delay_code;
    private String delivery_ref1_type;
    private String delivery_ref1;
    private String delivery_ref2_type;
    private String delivery_ref2;
    private String cod_payment_received;
    private String cod_payment_mode;
    private String cod_payment_ref1;
    private String cod_payment_ref2;
    private String seal_intact;
    private String seal_mismatch;
    private String actual_seal_number;
  }
}
