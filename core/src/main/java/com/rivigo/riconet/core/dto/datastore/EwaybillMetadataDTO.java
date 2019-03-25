package com.rivigo.riconet.core.dto.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class EwaybillMetadataDTO {

  private String ewaybillNumber;
  private String fromAddress;
  private String fromPob; // from Place of Business
  private String fromName;
  private String fromPincode;
  private String toAddress; // to Place of Business
  private String toPob;
  private String toName;
  private String toPincode;
}
