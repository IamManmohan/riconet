package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.Client;

public interface ClientMasterService {

  Client getClientById(Long id);

  Client getClientByCode(String code);

  void createUpdateClient(String dto);

  /**
   * This function is used to update epodApplicable flag on a client.
   *
   * @param payload that we receive from compass.
   * @return update the flag value in the clinet table.
   */
  void updateEpodDetails(String payload);
}
