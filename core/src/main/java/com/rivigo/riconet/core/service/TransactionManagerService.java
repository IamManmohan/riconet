package com.rivigo.riconet.core.service;

import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import java.util.Map;
import lombok.NonNull;

public interface TransactionManagerService {

  void hitTransactionManagerAndLogResponse(@NonNull String collectionRequestDtoJsonString);

  void syncExclusion(
      Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap,
      Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map);

  void syncPostUnpost(
      HandoverCollectionEventPayload handoverCollectionEventPayload, ZoomEventType eventType);

  void rollbackTransactionsAndLogResponse(@NonNull String collectionRequestDtoJsonString);
}
