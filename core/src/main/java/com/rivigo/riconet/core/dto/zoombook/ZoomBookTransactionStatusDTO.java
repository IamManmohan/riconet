package com.rivigo.riconet.core.dto.zoombook;

import com.rivigo.zoom.common.enums.zoombook.ZoomBookRequestReferenceType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionStatus;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ZoomBookTransactionStatusDTO {
    ZoomBookTransactionStatus transactionStatus;
    String txnNo;
    String clientRequestId;
    ZoomBookRequestReferenceType requestReferenceType;
    ZoomBookTransactionType transactionType;
    Double amount;
    String transactionHeader;
    String transactionSubHeader;
    String remarks;
    String reference;
    Long createdAt;
}