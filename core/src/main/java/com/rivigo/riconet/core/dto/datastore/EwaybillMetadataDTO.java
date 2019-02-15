package com.rivigo.riconet.core.dto.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EwaybillMetadataDTO {

  String ewaybillNumber;
  String fromAddress;
  String fromPob;
  String fromName;
  String fromPincode;
  String toAddress;
  String toPob;
  String toName;
  String toPincode;
}
