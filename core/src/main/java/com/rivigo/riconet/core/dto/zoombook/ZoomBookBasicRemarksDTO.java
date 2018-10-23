package com.rivigo.riconet.core.dto.zoombook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoomBookBasicRemarksDTO {

  // For each transaction made to zoomBook ,
  //   if view transaction dialog in finace UI has a formatter to read the remarks for that
  // specific transactionType
  //      UI will use the same
  //   else UI will show the comments
  private String comments;
}
