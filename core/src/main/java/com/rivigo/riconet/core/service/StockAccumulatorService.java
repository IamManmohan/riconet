package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.model.StockAccumulator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface StockAccumulatorService {

  List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole role, Long partnerId);

  List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(
      StockAccumulatorRole role, Long partnerId, OperationalStatus status);

  StockAccumulator getByUserId(Long userId);
}
