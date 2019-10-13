package com.rivigo.riconet.core.service;

import java.util.Map;

public interface BankTransferService {

  void createBankTransferTicket(Map<String, String> metadata);
}
