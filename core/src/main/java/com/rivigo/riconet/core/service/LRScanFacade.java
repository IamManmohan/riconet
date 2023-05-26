package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.logifreight.LRScanRequestDto;
import com.rivigo.riconet.core.dto.logifreight.LRScanResponseDto;

public interface LRScanFacade {
  LRScanResponseDto createConsignments(LRScanRequestDto lrScanRequestDto);
}
