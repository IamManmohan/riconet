package com.rivigo.riconet.core.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FlipkartErrorResponseDTO {
    private Long error_response_code;
    private Long error_internal_status_code;
    private String error_reason_code;
    private String error_description;
    private List<Object> error_stack;
    private Object additional_data;
}
