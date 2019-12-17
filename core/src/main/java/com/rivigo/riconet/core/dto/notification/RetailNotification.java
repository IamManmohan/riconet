package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.dto.SmsDTO;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.enums.RetailNotificationType;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RetailNotification {
  private String cnote;
  private Long edd;
  private String eddString;
  private BigDecimal totalAmount;
  private BigDecimal changeInAmount;
  private PaymentType paymentType;
  private String paymentModeString;
  private PaymentMode paymentMode;
  private Long fromOuId;
  private String fromOuCluster;
  private Long toOuId;
  private String toOuCluster;
  private Long ouId;
  private String ouCode;
  private String consigneePhone;
  private String consignorPhone;
  private Long userId;
  private String userName;
  private String userMobile;
  private Long drsId;
  private Long drsUserId;
  private String drsUserName;
  private String drsUserMobile;
  private Integer totalCnCount;
  private RetailNotificationType notificationType;
  private BigDecimal cashHanoveredAmount;
  private BigDecimal chequeHanoveredAmount;
  private Integer hanoveredCns;
  private String handoveredDateString;
  private BigDecimal cashPendingAmount;
  private BigDecimal chequePendingAmount;
  private List<SmsDTO> smsList;
}
