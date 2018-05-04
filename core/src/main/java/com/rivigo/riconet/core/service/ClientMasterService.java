package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.Client;
import org.springframework.stereotype.Service;

@Service
public interface ClientMasterService {

  Client getClientById(Long id);

  Client getClientByCode(String code);
}
