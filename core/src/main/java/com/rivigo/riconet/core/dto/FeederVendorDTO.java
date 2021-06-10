package com.rivigo.riconet.core.dto;

import com.rivigo.vms.enums.ExpenseType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.FeederVendor.VendorType;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

  // Expense type can RLH_FEEDER or NLH for vendor.
  private Set<ExpenseType> expenseTypeList;
}
