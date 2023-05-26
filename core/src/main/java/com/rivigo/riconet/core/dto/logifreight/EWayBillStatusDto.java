package com.rivigo.riconet.core.dto.logifreight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ewaybill.bridge.client.enums.EWayBillTransactionStatus;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EWayBillStatusDto {
  EWayBillTransactionStatus eWayBillStatus;
  String ewayBillNumber;
  String errorMessage;
}
