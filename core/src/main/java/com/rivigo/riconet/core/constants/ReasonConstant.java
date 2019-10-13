package com.rivigo.riconet.core.constants;

/** Created by chirag on 28/9/17. */
public class ReasonConstant {

  private ReasonConstant() {
    throw new IllegalStateException("Utility class");
  }

  public static final Long INTRANSIT_DELAY_REASON_ID = 1L;
  public static final String QC_BLOCKER_REASON =
      "Consignment Qc measurement values deviated from actual values";
  public static final String QC_BLOCKER_SUB_REASON =
      "Consignment Qc measurement values deviated from actual values";
  public static final String QC_VALIDATION_BLOCKER_REASON =
      "Consignment Qc validation is not complete";
  public static final String QC_VALIDATION_BLOCKER_SUB_REASON =
      "Consignment Qc validation is not complete";
  public static final String QC_MEASUREMENT_BLOCKER_REASON =
      "Consignment Qc measurement is not complete";
  public static final String QC_MEASUREMENT_BLOCKER_SUB_REASON =
      "Consignment Qc measurement is not complete";
  public static final String BANK_TRANSFER_BLOCKER_REASON = "Bank Transfer Payment issue";
  public static final String BANK_TRANSFER_BLOCKER_SUB_REASON = "Rejected in audit";
}
