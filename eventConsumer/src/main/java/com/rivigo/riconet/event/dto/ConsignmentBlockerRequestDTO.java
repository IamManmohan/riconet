package com.rivigo.riconet.event.dto;

import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ConsignmentBlockerRequestDTO {

  private Long consignmentId;
  private String reason;
  private String subReason;
  private Boolean isActive;
  private ConsignmentBlockerRequestType requestType;
}
