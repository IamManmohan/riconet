package com.rivigo.riconet.event.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChequeBounceDTO {

  @NotBlank private String cnote;

  @NotNull private Long consignmentId;

  @NotBlank private String bankName;

  @NotBlank private String chequeNumber;

  @NonNull private BigDecimal amount;
}
