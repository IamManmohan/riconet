package com.rivigo.riconet.core.enums;

public enum Clients {
  CORPORATE,
  RETAIL,
  BF;

  @Override
  public String toString() {
    return this.getClass().getSimpleName().toUpperCase() + "_" + super.toString();
  }
}
