package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/** @author Nikhil Rawat on 26/05/20. DTO for saving epod S3 url in consignment uploaded files. */
@Getter
@Setter
public class EpodPreparedDto {
  private String componentType;
  private String identifier;
  private String url;
}
