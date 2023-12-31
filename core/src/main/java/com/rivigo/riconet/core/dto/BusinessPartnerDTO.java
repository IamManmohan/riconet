package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.AvailabilityStatus;
import com.rivigo.zoom.common.enums.TacticalCreditStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BusinessPartnerDTO {

  private Long id;
  private String code;
  private String status;
  private String name;
  private String legalName;
  private AvailabilityStatus availabilityStatus;
  private TacticalCreditStatus tacticalCreditStatus;
  private Double tacticalCreditMinimumBalance;
  private Integer negativeTacticalCreditAllowedOverdueDays;
  private String type;
}
