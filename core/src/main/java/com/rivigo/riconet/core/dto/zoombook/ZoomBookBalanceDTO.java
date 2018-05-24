package com.rivigo.riconet.core.dto.zoombook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomBookBalanceDTO {
    Long orgId;
    BigDecimal balance;
    Long lastUpdatedAt;
    Long pending;
}
