package com.rivigo.riconet.core.dto.wms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.zoom.common.enums.TaskType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateAssignDto implements Serializable {
  private TaskType taskType;
  private List<String> userEmailList;
  private String locationCode;
  private Map<String, List<String>> taskEntityMap;
}
