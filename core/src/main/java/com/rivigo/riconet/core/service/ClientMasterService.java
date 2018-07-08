package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.Client;

public interface ClientMasterService {

  Client getClientById(Long id);

  Client getClientByCode(String code);

  void createUpdateClient(String dto);
}
