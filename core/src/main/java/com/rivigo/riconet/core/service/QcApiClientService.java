package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.QcResponseDTO;

/**
 * Created by ashfakh on 11/02/19.
 */
public interface QcApiClientService {

    QcResponseDTO getQcFlag(Long cnId);

}
