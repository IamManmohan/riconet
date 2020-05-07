package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import lombok.NonNull;

public interface TransactionManagerService {

  void hitTransactionManagerAndLogResponse(@NonNull CollectionRequestDto collectionRequestDto)
      throws JsonProcessingException;
}
