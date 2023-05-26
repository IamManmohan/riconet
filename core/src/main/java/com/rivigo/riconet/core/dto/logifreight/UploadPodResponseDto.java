package com.rivigo.riconet.core.dto.logifreight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadPodResponseDto {
  private LrPodDto lr;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class LrPodDto {
    private Long id;
    private String number;
  }
}
