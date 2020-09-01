package com.rivigo.riconet.core.constants;

public class ConsignmentConstant {

  /** flag to save whether an invoice is ProForma or Final. */
  public static final String IS_PRO_FORMA = "isProForma";

  private ConsignmentConstant() {
    throw new IllegalStateException("Utility class");
  }

  public static final String SECONDARY_CNOTE_SEPARATOR = "-";
  public static final long GLOBAL_ORGANIZATION = 9999;
  public static final long RIVIGO_ORGANIZATION_ID = 1;

  public static final Long CLIENT_DEFAULT_SAM_ID = 2570L; // ankit.kumar2@rivigo.com

  public static final String RETAIL_CLIENT_CODE = "ZRETL";

  public static final String CNOTE = "cnote";
  public static final String SHORT_URL = "shortUrl";
  public static final String URL = "url";
  public static final String METADATA = "metadata";

  /** Constant String to hold Normal CnoteType constant. */
  public static final String NORMAL_CNOTE_TYPE = "NORMAL";

  /** Constant String to hold Consignment status DELIVERED. */
  public static final String DELIVERED_STATUS = "DELIVERED";

  /** Constant interger to hold is_active of active consignment. */
  public static final int IS_ACTIVE_CONSIGNMENT = 1;
}
