package com.rivigo.riconet.core.constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WMSConstant {

  private WMSConstant() {
    log.error("Private constructor cannot be initialized");
  }

  public static final String RTO_REVERSE_TASK_TYPE = "RTO_REVERSE";

  public static final String CNOTE_ENTITY_TYPE = "CNOTE";
}
