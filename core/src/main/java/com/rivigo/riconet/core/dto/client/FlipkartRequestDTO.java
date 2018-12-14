package com.rivigo.riconet.core.dto.client;

import com.rivigo.riconet.core.dto.hilti.BaseHiltiFieldData;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FlipkartRequestDTO {
  private Map<String, String> metadata;
  private String referenceNumber;
  private String jobType;
  private String newStatusCode;
  private BaseHiltiFieldData fieldData;

  public FlipkartRequestDTO(HiltiRequestDto requestDto,List<String> barcodes) {
    this.setReferenceNumber(requestDto.getReferenceNumber());
    this.setJobType(requestDto.getJobType());
    this.setNewStatusCode(requestDto.getNewStatusCode());
    this.setFieldData(new BaseFlipkartFieldData(barcodes));
  }
}
