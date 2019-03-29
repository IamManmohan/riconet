package com.rivigo.riconet.core.dto.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.riconet.core.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatastoreResponseDto {

  private RequestStatus status;
  private String errorMessage;
  private Object payload;
  int statusCode;
}
