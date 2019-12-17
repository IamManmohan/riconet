package com.rivigo.riconet.core.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * created by mohak on 30/10/2019. this dto is same as `TemplateV2DTO` (only useful fields) of
 * zoom-communication, and stores the new template format used in `sendSmsV2` function in
 * `SmsServiceImpl`.
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TemplateV2DTO {
  private String name;
  private Integer version;
  private String language;
  private Map<String, String> params;
}
