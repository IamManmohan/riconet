package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ClientVasDetailsService;
import com.rivigo.zoom.common.enums.ClientVasType;
import com.rivigo.zoom.common.model.ClientVasDetail;
import com.rivigo.zoom.common.repository.mysql.ClientVasDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ashfakh on 13/09/18.
 */
public class ClientVasDetailsServiceImpl implements ClientVasDetailsService {

    @Autowired
    ClientVasDetailRepository clientVasDetailRepository;

    @Override
    public ClientVasDetail getClientVasDetails(Long clientId){
        List<ClientVasDetail> clientVasDetails = clientVasDetailRepository.findByClientIdAndClientVasTypeAndIsActive(clientId, ClientVasType.COD_DOD, Boolean.TRUE);
        return !CollectionUtils.isEmpty(clientVasDetails)
                        ? clientVasDetails.stream().
                        : "");
    }


}
