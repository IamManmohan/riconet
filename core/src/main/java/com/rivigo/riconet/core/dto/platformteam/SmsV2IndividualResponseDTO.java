package com.rivigo.riconet.core.dto.platformteam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** created by mohak on 31/10/2019. */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SmsV2IndividualResponseDTO {
  private String phoneNumber;
  private String response;
}
