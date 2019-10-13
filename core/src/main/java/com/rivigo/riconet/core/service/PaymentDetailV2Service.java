package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.PaymentDetailV2;

/** Created by ashfakh on 28/05/19. */
public interface PaymentDetailV2Service {
  String getRetailTypeFromConsignmentId(Long consignmentId);

  PaymentDetailV2 getByConsignmentId(Long consignmentId);
}
