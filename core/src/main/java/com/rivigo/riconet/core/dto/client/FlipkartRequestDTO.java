package com.rivigo.riconet.core.dto.client;

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
public class FlipkartRequestDTO extends HiltiRequestDto {
  private Map<String, String> metadata;
  private List<String> barcodes;

  public FlipkartRequestDTO(HiltiRequestDto requestDto) {
    this.setReferenceNumber(requestDto.getReferenceNumber());
    this.setJobType(requestDto.getJobType());
    this.setNewStatusCode(requestDto.getNewStatusCode());
    this.setFieldData(requestDto.getFieldData());
  }
}
