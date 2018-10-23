package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/** Created by ashfakh on 06/07/18. */
@Getter
@Setter
public class ConsignmentUploadedFilesDTO {
  private Long id;
  private String fileTypes;
  private Long consignmentId;
  private String s3URL;
  private String fileName;
}
