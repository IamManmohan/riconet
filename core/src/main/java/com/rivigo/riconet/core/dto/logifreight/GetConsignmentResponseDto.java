package com.rivigo.riconet.core.dto.logifreight;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class GetConsignmentResponseDto {

  List<LrModel> lrs;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class LrModel {
    private Lr lr;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class Lr {
    private String number;
    private int id;
    private String readableStatus;
    private boolean isFullLoad;
    private String providedServiceName;
    private String ref1;
    private String ref2;
    private String trackingHash;
    private String deliveredAt;
    private String deliveredAtLocation;
    private String deliveredToContactName;
    private String lastEventAt;
    private String dispatchDate;
    private String readyForPickupAt;
    private String trackingStatus;
    private String createdAt;
    private double balanceToPayAmount;
    private double codAmount;
    private String displayEta;
    private String originalEta;
    private String currentEta;
    private String invoiceNumber;
    private String displayDeliveryDate;
    private String trackingHashUrl;
  }
}
