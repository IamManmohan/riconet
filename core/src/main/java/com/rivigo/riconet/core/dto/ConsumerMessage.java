package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerMessage {
  private String id;
  private Long createdAt;
  private Long lastUpdatedAt;
  private String topic;
  private String message;
  private Long retryCount;
  private String errorMsg;
}
