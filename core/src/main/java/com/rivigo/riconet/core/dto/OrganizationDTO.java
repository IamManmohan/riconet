package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by ashfakh on 21/6/18. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
  private Long id;
  private String code;
  private String oldCode;
  private String name;
  private OrganizationType type;
  private OperationalStatus status;
  private Boolean insuranceApplicable;
  private Boolean disableRetailBooking;
  private String email;
  private String mobileNumber;
  private Long samUserId;
  private Long samLeadId;
  private Boolean fodApplicable;
  private Boolean codDodApplicable;
}
