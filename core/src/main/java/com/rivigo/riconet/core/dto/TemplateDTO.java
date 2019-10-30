package com.rivigo.riconet.core.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** created by mohak on 30/10/2019. */
@Getter
@Setter
@Builder
@ToString
public class TemplateDTO {
  private String name;
  private Integer version;
  private String language;
  private Map<String, String> params = new HashMap<>();
}
