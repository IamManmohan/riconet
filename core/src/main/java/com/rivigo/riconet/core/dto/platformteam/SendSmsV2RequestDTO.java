package com.rivigo.riconet.core.dto.platformteam;

import com.rivigo.riconet.core.dto.TemplateV2DTO;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SendSmsV2RequestDTO {
  private String client;
  private List<String> phoneNumbers;
  private TemplateV2DTO template;
}
