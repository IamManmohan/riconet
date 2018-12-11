package com.rivigo.riconet.core.dto.client;

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
@Builder
@ToString
public class ClientIntegrationResponseDTO {
    private Boolean success;
    private String success_code;
    private String success_description;
    private String http_status;
    private FlipkartErrorResponseDTO error_response;
}
