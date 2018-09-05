package com.rivigo.riconet.core.constants;

public class ConsignmentConstant {

  private ConsignmentConstant() {
    throw new IllegalStateException("Utility class");
  }

  public static final String SECONDARY_CNOTE_SEPARATOR = "-";
  public static final int MANDATORY_STOCK_CHECK_HOURS = 2;
  public static final int DO_STOCK_CHECK_DAYS = 90;
  public static final int SHOW_STOCK_CHECK_DAYS = 7;
  public static final long GLOBAL_ORGANIZATION = 9999;
  public static final long RIVIGO_ORGANIZATION_ID = 1;
  public static final Long CLIENT_DEFAULT_SAM_ID = 50059L; // anmol.batra@rivigo.com
  public static final long MAX_DF_ORGANIZATION_ID = 10001;
  public static final String RETAIL_CLIENT_CODE = "ZRETL";

  public static final String BUSINESS = "Business";
}
