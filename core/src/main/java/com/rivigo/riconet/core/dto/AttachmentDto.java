package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

/** Created by chiragbansal on 25/06/18. */
@Getter
@Setter
public class AttachmentDto {

  private byte[] data;
  private String url;
  private String name;
  private String type;
}
