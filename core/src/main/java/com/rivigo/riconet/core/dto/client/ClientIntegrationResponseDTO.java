package com.rivigo.riconet.core.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonProperty;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientIntegrationResponseDTO {
  private Boolean success;

  @JsonProperty("success_code")
  private String successCode;

  @JsonProperty("success_description")
  private String successDescription;

  @JsonProperty("http_status")
  private String httpStatus;

  @JsonProperty("error_response")
  private FlipkartErrorResponseDTO errorResponse;
}
