package com.rivigo.riconet.core.dto.hilti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiltiResponseDto {
  private Long successCount;
  private Long failCount;
  private List<String> successList;
  private List<String> successIdList;
  private List<String> failureList;
  private List<String> successMessage;
  private List<String> failMessage;
  private List<String> failedJobs;
  private List<String> excelHeading;
  private Object failedDataStoreRecords;
}
