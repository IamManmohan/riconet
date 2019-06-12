package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import org.springframework.beans.factory.annotation.Autowired;

/** Created by ashfakh on 28/05/19. */
public class PaymentDetailV2ServiceImpl implements PaymentDetailV2Service {

  @Autowired private PaymentDetailV2Service paymentDetailV2Service;

  @Override
  public String getRetailTypeFromConsignmentId(Long consignmentId) {
    return paymentDetailV2Service.getRetailTypeFromConsignmentId(consignmentId);
  }
}
