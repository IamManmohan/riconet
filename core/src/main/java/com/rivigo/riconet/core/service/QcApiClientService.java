package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.qc.QcRequestDTO;
import com.rivigo.riconet.core.dto.qc.QcResponseDTO;

/** Created by ashfakh on 11/02/19. */
public interface QcApiClientService {

  QcResponseDTO getQcFlag(QcRequestDTO qcRequestDTO);
}
