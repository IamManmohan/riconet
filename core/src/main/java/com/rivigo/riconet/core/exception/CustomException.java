package com.rivigo.riconet.core.exception;

public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CustomException() {
    super();
  }

  public CustomException(String message) {
    super(message);
  }

  public CustomException(Throwable cause) {
    super(cause);
  }

  public CustomException(String message, Throwable cause) {
    super(message, cause);
  }

  public CustomException(String message, Object... variables) {
    super(String.format(message, variables));
  }
}
