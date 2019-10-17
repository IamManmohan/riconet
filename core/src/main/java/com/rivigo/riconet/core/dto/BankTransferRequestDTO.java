package com.rivigo.riconet.core.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class BankTransferRequestDTO {

  @NonNull @NotBlank private String transactionReferenceNo;

  @NonNull @NotBlank private String bankAccountReference;
}
