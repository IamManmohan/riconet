package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorContractZoomEventDTO {
  private String vendorCode;
  private String expenseType;
  private String serviceRequestType;
  private String vendorContractStatus;
  private Boolean siteExpenseActive;
}
