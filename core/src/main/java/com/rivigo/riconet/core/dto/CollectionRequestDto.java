package com.rivigo.riconet.core.dto;

import com.rivigo.riconet.core.enums.CollectionEventType;
import com.rivigo.zoom.common.enums.PaymentMode;
import lombok.Data;

@Data
public class CollectionRequestDto {

  private Long consignmentId;
  private String cnote;
  private CollectionEventType eventType;
  private Long amount;
  private String pickupOuCode;
  private String bankTransferPendingApproval;

  private PaymentMode paymentType;
  private String clientCode;
  private String captainCode;
  private String bpBookCode;
  private String bpWalletCode;
  private String cmsIdentifier;

  private String deliveryOuCode;
}
