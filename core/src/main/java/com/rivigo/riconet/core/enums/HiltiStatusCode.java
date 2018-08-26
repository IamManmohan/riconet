package com.rivigo.riconet.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HiltiStatusCode {
  PICKUP_DONE,
  ARRIVED,
  DISPATCHED,
  OUT_FOR_DELIVERY,
  DELIVERED,
  NOT_DELIVERED,
  RTO;

  @Override
  @JsonValue
  public String toString() {
    return this.name().toLowerCase();
  }
}
