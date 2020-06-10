package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.PaymentDetailV2;
import java.util.Collection;
import java.util.List;

/** Created by ashfakh on 28/05/19. */
public interface PaymentDetailV2Service {
  String getRetailTypeFromConsignmentId(Long consignmentId);

  PaymentDetailV2 getByConsignmentId(Long consignmentId);

  /**
   * This function is used to fetch list of payment detail v2 based on list of consignment ids.
   *
   * @param consignmentIds list of consignment ids.
   * @return list of payment detail v2.
   */
  List<PaymentDetailV2> getByConsignmentIdIn(Collection<Long> consignmentIds);

  List<PaymentDetailV2> getByTransactionReferenceNo(String transactionReferenceNo);
}
