package com.rivigo.riconet.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomCommunicationsEventDTO {

  private String eventName;

  private Long entityId;

  private String entityName;

  private String eventGUID;

  private Long tsMs;

  private String eventUID;

  private Map<String, String> metadata;

  private List<Object> subscribers;

  private List<String> conditions;
}
