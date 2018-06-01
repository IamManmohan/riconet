package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.repository.mysql.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientMasterServiceImpl implements ClientMasterService {

  @Autowired ClientRepository clientRepository;

  @Override
  public Client getClientById(Long id) {
    return clientRepository.findOne(id);
  }

  @Override
  public Client getClientByCode(String code) {
    return clientRepository.findByClientCode(code);
  }
}
