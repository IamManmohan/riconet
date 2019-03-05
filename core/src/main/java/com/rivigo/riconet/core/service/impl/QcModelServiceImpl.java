package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.qc.QcRequestDTO;
import com.rivigo.riconet.core.dto.qc.QcResponseDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.QcApiClientService;
import com.rivigo.riconet.core.service.QcModelService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 12/02/19. */
@Service
@Slf4j
public class QcModelServiceImpl implements QcModelService {

  @Autowired private QcApiClientService qcApiClientService;

  @Autowired private ExecutorService executorService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Override
  public Boolean getQcValidationFlag(Long cnId) {
    try {
      log.info("Calling Qc model for Cn Id : {}", cnId);
      QcRequestDTO qcRequestDTO = new QcRequestDTO();
      qcRequestDTO.setConsignmentId(cnId);
      Long qcApiTimeOut = zoomPropertyService.getLong(ZoomPropertyName.QC_MODEL_API_TIMEOUT, 1000L);
      final Future<QcResponseDTO> f =
          executorService.submit(() -> qcApiClientService.getQcFlag(qcRequestDTO));
      QcResponseDTO qcResponseDTO = f.get(qcApiTimeOut, TimeUnit.MILLISECONDS);
      log.info(
          "Response from QC model for Cn Id : {}, QC needed : {}, Reason : {}",
          cnId,
          qcResponseDTO.getDecision(),
          qcResponseDTO.getDisposition());
      return getValue(qcResponseDTO.getDecision());
    } catch (TimeoutException e) {
      log.error("QC model API Call Timed out for {}, Exception :{}", cnId, e);
      return Boolean.FALSE;
    } catch (Exception e) {
      log.error("Error calling QC model API {}", e);
      return Boolean.FALSE;
    }
  }

  private Boolean getValue(Integer integer) {
    if (integer == 1) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
