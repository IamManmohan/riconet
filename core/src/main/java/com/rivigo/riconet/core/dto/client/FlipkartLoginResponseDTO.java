package com.rivigo.riconet.core.dto.client;

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
public class FlipkartLoginResponseDTO {
    private Boolean success;
    private Map<String, String> data;
    @JsonProperty("http_status")
    private String httpStatus;
    private FlipkartErrorResponseDTO errorResponseDTO;
}
