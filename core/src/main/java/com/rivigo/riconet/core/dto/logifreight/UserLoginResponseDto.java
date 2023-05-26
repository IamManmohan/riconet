package com.rivigo.riconet.core.dto.logifreight;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserLoginResponseDto {
  private UserDto user;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class UserDto {
    @JsonProperty("api_key")
    private String apiKey;

    private Long companyId;
    private String email;
    private Long id;
    private String login;
    private String mobileNumber;
    private String name;
    private String companyName;
    private String companyCode;
    private String mobileLabelPrinterCode;
    private boolean isDriver;
    private String facilityPrintName;
    private String facilityName;
    private String facilityCode;
  }
}
