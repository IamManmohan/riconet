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

  /**
   * This function adds / remove blocker for a client who has breached its credit limit.
   *
   * @param payload contains the payload in the string format.
   */
  void updateClientBlocker(String payload);
}
