package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class BankTransferRequestDTO {

  @NonNull @NotBlank private String transactionReferenceNo;

  @NonNull @NotBlank private String bankAccountReference;
}
