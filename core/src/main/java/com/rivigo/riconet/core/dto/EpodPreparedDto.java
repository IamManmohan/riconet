package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for saving epod S3 url in consignment uploaded files.
 *
 * @author Nikhil Rawat on 26/05/20.
 */
@Getter
@Setter
public class EpodPreparedDto {
  /** variable for storing componentType */
  private String componentType;
  /** variable for storing identifier which is CNOTE number */
  private String identifier;
  /** variable for storing s3 url of the e-pod document */
  private String url;
}
