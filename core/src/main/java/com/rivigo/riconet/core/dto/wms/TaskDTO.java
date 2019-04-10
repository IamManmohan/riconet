package com.rivigo.riconet.core.dto.wms;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Builder
@Getter
@Setter
public class TaskDTO {
  private String locationCode;
  private DateTime scheduledEndTime;
  private Map<String, List<String>> taskEntityMap;
  private String taskType;
  private String userEmail;
}
