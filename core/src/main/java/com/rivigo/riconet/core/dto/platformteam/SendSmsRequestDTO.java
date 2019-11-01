package com.rivigo.riconet.core.dto.platformteam;

import com.rivigo.riconet.core.dto.TemplateDTO;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SendSmsRequestDTO {
  private String client;
  private List<String> phoneNumbers;
  private TemplateDTO template;
}
