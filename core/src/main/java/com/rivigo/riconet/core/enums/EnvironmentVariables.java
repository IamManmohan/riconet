package com.rivigo.riconet.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** created by mohak on 06/11/2019. */
@Getter
@AllArgsConstructor
public enum EnvironmentVariables {
  ENVIRONMENT_PRODUCTION_PROPERTY_NAME("production");

  private String label;
}
