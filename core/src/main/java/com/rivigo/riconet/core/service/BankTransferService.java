package com.rivigo.riconet.core.service;

import java.util.Map;

/** BankTransferService is used to handle all tasks related to payment type BANK_TRANSFER. */
public interface BankTransferService {

  /**
   * This function is used to create UTR and cnote level ticket in zoom-ticketing for payment type
   * Bank Transfer. <br>
   * This flow ensures Backward compatibility.
   */
  void createTicket(Map<String, String> metadata);

  /**
   * This function handles incoming UniqueTransactionReferencePosting event from compass. <br>
   * Bases on UniqueTransactionReferencePostingStatus, either knockoff or revert knockoff request is
   * sent to backend.
   */
  void handleUniqueTransactionReferencePostingEvent(String payload);
}
