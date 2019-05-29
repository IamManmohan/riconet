package com.rivigo.riconet.core.dto;

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
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDto implements Serializable {

  private Long id;
  private TaskType taskType;
  private String userEmail;
  private String locationCode;
  private DateTime scheduledEndTime;
  private Map<String, List<String>> taskEntityMap;
}
