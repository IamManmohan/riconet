package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.dto.RetailNotificationDTO;
import com.rivigo.zoom.common.dto.zoombook.TransactionModelDTO;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.mongo.RetailNotification;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface RetailService {

  void processRetailNotificationDTOList(List<RetailNotificationDTO> retailNotificationDTOList);

  void getPendingHandoverConsignments(
      RetailNotification notification, List<TransactionModelDTO> transactionModelDTOList);

  Map<Long, PaymentDetailV2> getPaymentdetailsByConsignmentIdIn(List<Long> consignmentIds);
}
