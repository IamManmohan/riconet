package com.rivigo.riconet.core.dto.platformteam;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** created by mohak on 30/10/2019. */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsResponseDTO {
  private Long Code;
  private String Message;
  private List<SmsIndividualResponseDTO> bulkResponse;
}
