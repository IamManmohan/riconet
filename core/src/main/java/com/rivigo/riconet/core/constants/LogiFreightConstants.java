package com.rivigo.riconet.core.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogiFreightConstants {
  public static final String DELIVERY_USER_LOGIN_TOKEN_KEY =
      "RICONET_LOGIFREIGHT_DELIVERY_USER_LOGIN_TOKEN";
  public static final String RELEASE_USER_LOGIN_TOKEN_KEY =
      "RICONET_LOGIFREIGHT_RELEASE_USER_LOGIN_TOKEN";
  public static final String USER_LOGIN_URL = "/integration/users/login.json";
  public static final String GET_CONSIGNMENT_DETAIL_URL = "/integration/consignments/list.json";

  public static final Long USER_LOGIN_KEY_CACHE_DURATION_IN_MILLIS = 1000 * 60 * 60 * 12L;

  public static final String LOGIFREIGHT_API_KEY_HEADER_NAME = "X-SHIPX-API-KEY";
  public static final String USERAGENT_HEADER = "user-agent";
  public static final String USERAGENT_HEADER_BROWSER_VALUE =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

  public static final String RECORD_DELIVERY_URL = "/integration/consignments/deliver.json";

  public static final String UPLOAD_POD_URL = "/integration/consignments/attach";

  public static final String CONSIGNMENT_NUMBER_REQUEST_PARAM = "consignment_number";

  public static final String ATTACHMENT_TYPE_REQUEST_PARAM = "attachment_type";

  public static final String FILEPATH_REQUEST_PARAM = "file";

  public static final String IS_VERIFIED_REQUEST_PARAM = "is_verifed";

  public static final String IS_VERIFIED_VALUE = "Yes";

  public static final String UPLOAD_POD_TYPE = "EPOD";

  public static final String RELEASE_HOLD_URL = "/integration/consignments/release.json";
}
