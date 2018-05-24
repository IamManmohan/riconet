package com.rivigo.riconet.core.dto.zoombook;

import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ZoomBookTransactionResponseDTO {
    private ZoomBookTransactionStatus transactionStatus;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal amount;
    private String txnNo;
    private String clientRequestId;
    private String reference;
}
