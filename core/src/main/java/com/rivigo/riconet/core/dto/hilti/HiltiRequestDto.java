package com.rivigo.riconet.core.dto.hilti;

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
@ToString
@Builder
public class HiltiRequestDto {
  private String referenceNumber;
  private String jobType;
  private String newStatusCode;
  private BaseHiltiFieldData fieldData;
}
