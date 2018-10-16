package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ClientVasDetailsService;
import com.rivigo.zoom.common.enums.ClientVasType;
import com.rivigo.zoom.common.model.ClientVasDetail;
import com.rivigo.zoom.common.repository.mysql.ClientVasDetailRepository;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/** Created by ashfakh on 13/09/18. */
@Slf4j
@Service
public class ClientVasDetailsServiceImpl implements ClientVasDetailsService {

  @Autowired private ClientVasDetailRepository clientVasDetailRepository;

  @Override
  public ClientVasDetail getClientVasDetails(Long clientId) {
    List<ClientVasDetail> clientVasDetails =
        clientVasDetailRepository.findByClientIdAndClientVasTypeAndIsActive(
            clientId, ClientVasType.COD_DOD, Boolean.TRUE);
    return !CollectionUtils.isEmpty(clientVasDetails)
            && clientVasDetails
                .stream()
                .max(Comparator.comparing(ClientVasDetail::getCreatedAt))
                .isPresent()
        ? clientVasDetails.stream().max(Comparator.comparing(ClientVasDetail::getCreatedAt)).get()
        : null;
  }
}
