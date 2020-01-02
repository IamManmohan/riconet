package com.rivigo.riconet.core.dto.client;

import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class FlipkartRequestDTO extends HiltiRequestDto {
  private Map<String, String> metaData;

  public FlipkartRequestDTO(HiltiRequestDto requestDto) {
    this.setReferenceNumber(requestDto.getReferenceNumber());
    this.setJobType(requestDto.getJobType());
    this.setNewStatusCode(requestDto.getNewStatusCode());
    this.setFieldData(requestDto.getFieldData());
  }
}
