package com.rivigo.riconet.core.dto.logifreight;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LRScanRequestDto {
  List<String> clientReferenceNumbers;
}
