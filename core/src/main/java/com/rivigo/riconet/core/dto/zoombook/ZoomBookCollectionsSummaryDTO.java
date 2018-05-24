package com.rivigo.riconet.core.dto.zoombook;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoomBookCollectionsSummaryDTO {
    Long consignmentId;
    String cnote;
    String paymentType;
    Long collectionTime;
    String clientName;
    String clientNumber;
    String handoverTransactionId;
    Long handoverTime;
    String handoverRecieverName;
    String handoverLocationCode;
    BigDecimal totalAmount;
    String paymentMode;
    String bankName;
    String chequeNumber;
    String collectUserName;
    String collectUserMobile;
    Long  collectUserId;

}
