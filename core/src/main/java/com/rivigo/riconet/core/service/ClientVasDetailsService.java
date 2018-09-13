package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ClientVasDetail;

/**
 * Created by ashfakh on 13/09/18.
 */
public interface ClientVasDetailsService {
    ClientVasDetail getClientVasDetails(Long clientId);
}
