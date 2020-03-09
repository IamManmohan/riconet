package com.rivigo.riconet.core.constants;

public final class RestUtilConstants {

  private RestUtilConstants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String TOKEN_PREFIX = "Bearer ";
  public static final long DEFAULT_TIMEOUT_MILLIS = 5000L;
  public static final int DEFAULT_RETRY_ATTEMPTS = 2;
  public static final int MAX_RETRY_ATTEMPTS = 5;
  public static final String X_CLIENT_ID = "X_CLIENT_ID";
  public static final String TENANT_ID = "X_TENANT_ID";
}
