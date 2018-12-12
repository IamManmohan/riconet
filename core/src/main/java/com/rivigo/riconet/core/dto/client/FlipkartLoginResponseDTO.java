package com.rivigo.riconet.core.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlipkartLoginResponseDTO {
    private Boolean success;
    private Map<String, String> data;
    @JsonProperty("http_status")
    private String httpStatus;
    @JsonProperty("error_response")
    private FlipkartErrorResponseDTO errorResponseDTO;
}
