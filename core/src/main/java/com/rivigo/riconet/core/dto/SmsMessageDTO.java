package com.rivigo.riconet.core.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SmsMessageDTO {
  private List<String> phoneNumbers;
  private String message;
}
