package com.rivigo.riconet.core.dto.zoombook;

import java.math.BigDecimal;
import java.util.List;
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
public class ZoomBookCollectionsHandoverDTO {

    Long userId;
    String userName;
    String userMobile;
    String date;
    BigDecimal totalCashCollections;
    BigDecimal totalChequeCollections;
    Integer totalCNs;
    BigDecimal handoveredCashAmount;
    BigDecimal handoveredChequeAmount;
    Integer handoveredCNs;
    BigDecimal pendingCashAmount;
    BigDecimal pendingChequeAmount;
    Integer pendingCNs;
    Long createdAtTime;
    List<ZoomBookCollectionsHandoverDTO> zoomBookCollectionsHandoverDTOList;
}
