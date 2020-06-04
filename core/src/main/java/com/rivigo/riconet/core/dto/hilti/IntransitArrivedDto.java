package com.rivigo.riconet.core.dto.hilti;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IntransitArrivedDto extends BaseHiltiFieldData {
  private String atDestination;
  private String arrivedAt;
}
