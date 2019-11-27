package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.repository.mysql.PaymentDetailV2Repository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 28/05/19. */
@Slf4j
@Service
public class PaymentDetailV2ServiceImpl implements PaymentDetailV2Service {

  @Autowired private PaymentDetailV2Repository paymentDetailV2Repository;

  @Override
  public String getRetailTypeFromConsignmentId(Long consignmentId) {
    return paymentDetailV2Repository.getRetailTypeByConsignmentIdAndIsActive(consignmentId);
  }

  @Override
  public PaymentDetailV2 getByConsignmentId(Long consignmentId) {
    return paymentDetailV2Repository.findByConsignmentIdAndIsActive(consignmentId, true);
  }

  @Override
  public List<PaymentDetailV2> getByTransactionReferenceNo(String transactionReferenceNo) {
    return paymentDetailV2Repository.findByTransactionReferenceNoAndIsActiveTrue(
        transactionReferenceNo);
  }
}
