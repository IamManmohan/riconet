package com.rivigo.riconet.core.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** created by mohak on 30/10/2019. */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDTO {
  private String name;
  private Integer version;
  private String language;
  private Map<String, String> params;
}
