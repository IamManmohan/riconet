package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.FeederVendor.VendorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeederVendorDTO {
  private Long id;

  private String vendorName;

  private String vendorCode;

  private String email;

  private String legalName;

  private String personName;

  private String contactNumber;

  private Long clusterId;

  private VendorType vendorType;

  private OperationalStatus vendorStatus;

  private String clusterCode;
}
