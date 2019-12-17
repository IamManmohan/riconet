package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.AvailabilityStatus;
import com.rivigo.zoom.common.enums.TacticalCreditStatus;
import java.util.List;
import java.util.Set;
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
  private Set<String> type;
  private List<LocalityDTO> localityDTOList;
  private List<String> pincodeList;
}
