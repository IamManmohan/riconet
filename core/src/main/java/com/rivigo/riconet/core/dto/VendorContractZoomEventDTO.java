package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorContractZoomEventDTO {
  private String vendorCode;
  private ExpenseType expenseType;
  private String serviceRequestType;
  private VendorContractStatus vendorContractStatus;
  private Boolean siteExpenseActive;
}
