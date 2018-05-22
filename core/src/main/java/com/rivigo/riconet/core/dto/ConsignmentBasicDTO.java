package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.ConsignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsignmentBasicDTO {

  private String cnote;
  private Long consignmentId;
  private Long locationId;
  private ConsignmentStatus status;
}