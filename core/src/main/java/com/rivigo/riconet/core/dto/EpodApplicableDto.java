package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/** @author Nikhil Rawat on 26/05/20. DTO for toggling epodApplicable flag on clinet */
@Getter
@Setter
public class EpodApplicableDto {
  private String clientCode;
  private String serviceType;
  private String electronicPODApplicability;
}
