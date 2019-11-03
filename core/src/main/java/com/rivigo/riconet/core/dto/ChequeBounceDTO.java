package com.rivigo.riconet.core.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
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

  @NotBlank private String bankName;

  private String bankAccountReference;

  @NotBlank private String chequeNumber;

  @NonNull private BigDecimal amount;
}
