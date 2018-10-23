package com.rivigo.riconet.core.dto.client;

import lombok.Getter;
import lombok.Setter;

/** Created by ashfakh on 18/6/18. */
@Setter
@Getter
public class BillingEntityDTO {
  private Long id;
  private Long clientId;
  private String entityName;
  private Boolean isActive;
}
