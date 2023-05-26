package com.rivigo.riconet.core.dto.logifreight;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class GetConsignmentRequestDto {
  private CreateConsignmentDto lr;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class CreateConsignmentDto {
    private Collection<String> number;
  }
}
