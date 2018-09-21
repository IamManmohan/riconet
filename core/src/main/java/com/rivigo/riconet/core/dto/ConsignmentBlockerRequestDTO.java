package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsignmentBlockerRequestDTO {

  private Long consignmentId;
  private String reason;
  private String subReason;
  private Boolean isActive;
  private ConsignmentBlockerRequestType requestType;
}
