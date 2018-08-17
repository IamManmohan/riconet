package com.rivigo.riconet.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HiltiJobType {
  PICKUP,
  INTRANSIT,
  DELIVERY;

  @Override
  @JsonValue
  public String toString() { return name().toLowerCase(); }
}
