package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.qc.QcRequestDTO;
import com.rivigo.riconet.core.dto.qc.QcResponseDTO;
import com.rivigo.riconet.core.service.QcApiClientService;
import com.rivigo.riconet.core.service.QcModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by ashfakh on 12/02/19.
 */

@Service
@Slf4j
public class QcModelServiceImpl implements QcModelService {

    @Autowired private QcApiClientService qcApiClientService;

    @Async
    public void getAndLogQcFlagInAsync(Long cnId) {
        try {
            log.info("Calling Qc model for Cn Id : {}", cnId);
            QcRequestDTO qcRequestDTO = new QcRequestDTO();
            qcRequestDTO.setConsignmentId(cnId);
            QcResponseDTO qcResponseDTO = this.qcApiClientService.getQcFlag(qcRequestDTO);
            log.info(
                    "Response from QC model for Cn Id : {}, QC needed : {}, Reason : {}",
                    cnId,
                    qcResponseDTO.getDecision(),
                    qcResponseDTO.getDisposition());
        } catch (Exception e) {
            log.error("Error calling QC model API {}", e);
        }
    }
}
