package com.rivigo.riconet.core.constants;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class ClientConstants {

  private ClientConstants() {
    throw new IllegalStateException("Utility class");
  }

  // TODO: get this corrected
  public static final String LOGI_FREIGHT_CLIENT_ID = "9721";
  public static final String PFIZER_CLIENT_ID = "10019";
  public static final String PFIZER_CLIENT_ID_IND = "10020";
  public static final String HILTI_CLIENT_ID = "619";
  public static final String HILTI_CLIENT_ID_DEP = "2187";
  public static final String HILTI_CLIENT_ID_CD = "2870";
  public static final String FLIPKART_SELLER_CLIENT = "2253";
  public static final String FLIPKART_INDIA_CLIENT = "3742";
  public static final String FLIPKART_INDIA_ZOOM_CLIENT = "4128";
  public static final String FLIPKART_INTERNET_SELLER_CLIENT = "5522";
  public static final String ZOOM_DOCS_CONSIGNMENT_CLIENT_CODE = "ZDOCS";

  public static final String CORPORATE_CLIENT_RETURN_CN_MOVEMENT_TYPE = "RETURN";

  public static final String SERVICE_POC_STRING = "SERVICE_POC";

  public static final BigInteger POC_LEVEL_FOR_EMAIL = BigInteger.valueOf(1);

  public static final String CONSIGNER_VALUE = "CONSIGNER";

  public static final List<String> PFIZER_CLIENT_IDS =
      Arrays.asList(PFIZER_CLIENT_ID, PFIZER_CLIENT_ID_IND);
}
