package com.rivigo.riconet.core.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlipkartErrorResponseDTO {
  @JsonProperty("error_response_code")
  private Long errorResponseCode;

  @JsonProperty("error_internal_status_code")
  private Long errorInternalStatusCode;

  @JsonProperty("error_reason_code")
  private String errorReasonCode;

  @JsonProperty("error_description")
  private String errorDescription;

  @JsonProperty("error_stack")
  private List<Object> errorStack;

  @JsonProperty("additional_data")
  private Object additionalData;
}
