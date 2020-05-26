package com.rivigo.riconet.core.service;

import com.rivigo.collections.api.dto.HandoverCollectionEventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import java.util.Map;
import lombok.NonNull;

public interface TransactionManagerService {

  /**
   * This function hits transaction manager with collectionRequestDtoJsonString and logs the
   * response.
   *
   * @param collectionRequestDtoJsonString request json string to be sent to transaction manager.
   */
  void hitTransactionManagerAndLogResponse(@NonNull String collectionRequestDtoJsonString);

  /**
   * This function fetches pickup user and drs user and sends events.
   *
   * @param cnIdToConsignmentMap consignment id to consignment mapping
   * @param cnIdToPaymentDetailV2Map consignment id to pdv2 mapping
   */
  void syncExclusion(
      Map<Long, ConsignmentReadOnly> cnIdToConsignmentMap,
      Map<Long, PaymentDetailV2> cnIdToPaymentDetailV2Map);

  /**
   * This functions fetches cnIds, cnId to consignment read only mapping, list of payment detail v2
   * and consignment schedule.
   *
   * @param handoverCollectionEventPayload collections handover payload.
   * @param eventType zoom event type.
   */
  void syncPostUnpost(
      HandoverCollectionEventPayload handoverCollectionEventPayload, ZoomEventType eventType);

  /**
   * This function hits transaction manager and rollbackTransactions with
   * collectionRequestDtoJsonString and logs the response.
   *
   * @param collectionRequestDtoJsonString request json string to be sent to transaction manager.
   */
  void rollbackTransactionsAndLogResponse(@NonNull String collectionRequestDtoJsonString);
}
