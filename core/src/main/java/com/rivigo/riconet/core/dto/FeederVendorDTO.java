package com.rivigo.riconet.core.dto;

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

  private String personName;

  private Long contactNumber;

  private Long clusterId;

  private VendorType vendorType;

  private String vendorStatus;

  private String clusterCode;
}
