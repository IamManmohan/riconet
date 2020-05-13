package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import java.util.Map;
import lombok.NonNull;

public interface TransactionManagerService {

  void hitTransactionManagerAndLogResponse(@NonNull CollectionRequestDto collectionRequestDto);

  void syncExclusion(Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap);
}
