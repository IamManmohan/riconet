package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.AvailabilityStatus;
import com.rivigo.zoom.common.enums.TacticalCreditStatus;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessPartnerDTO {

  private Long id;
  private String code;
  private String status;
  private String name;
  private String legalName;
  private AvailabilityStatus availabilityStatus;
  private TacticalCreditStatus tacticalCreditStatus;
  private Double tacticalCreditMinimumBalance;
  private String type;
  private List<LocalityDTO> localityDTOList;
  private List<String> pincodeList;
}
