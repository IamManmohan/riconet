package com.rivigo.riconet.core.service;

import java.util.Map;

public interface BankTransferService {

  void createTicket(Map<String, String> metadata);
}
