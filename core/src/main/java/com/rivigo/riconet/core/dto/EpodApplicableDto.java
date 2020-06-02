package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for toggling epodApplicable flag on client.
 *
 * @author Nikhil Rawat on 26/05/20.
 */
@Getter
@Setter
public class EpodApplicableDto {
  /** variable for storing client code. */
  private String clientCode;
  /** variable for storing serviceType. */
  private String serviceType;
  /** variable for storing the e-pod applicability flag. */
  private String electronicPODApplicability;
}
