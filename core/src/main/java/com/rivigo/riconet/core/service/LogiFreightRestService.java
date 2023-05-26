package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryRequestDto;
import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryResponseDto;
import com.rivigo.riconet.core.dto.logifreight.ReleaseLrHoldResponseDto;
import com.rivigo.riconet.core.dto.logifreight.UploadPodResponseDto;

public interface LogiFreightRestService {

  String getUserLoginToken(String email, String password, String cacheKey);

  ReleaseLrHoldResponseDto releaseLrHold(String lrNumber);

  RecordDeliveryResponseDto recordConsignmentDelivery(RecordDeliveryRequestDto requestDto);

  UploadPodResponseDto uploadPod(String lrNumber, String path);
}
