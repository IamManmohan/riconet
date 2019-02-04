package com.rivigo.riconet.core.dto;

import com.rivigo.riconet.core.enums.TaskStatus;
import com.rivigo.zoom.common.enums.TaskType;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class TaskDto implements Serializable {

  private Long id;
  private String locationCode;
  private TaskType taskType;
  private String displayName;
  private String userEmail;
  private TaskStatus status;
  private LocalDateTime scheduledEndTime;
  private LocalDateTime assignedAt;
  private Long toliId;
  private Long parentTaskId;
}
