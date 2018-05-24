package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.StockAccumulatorService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.repository.mysql.StockAccumulatorRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockAccumulatorServiceImpl implements StockAccumulatorService {

  @Autowired private StockAccumulatorRepository stockAccumulatorRepo;

  @Override
  public List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerId(
      StockAccumulatorRole role, Long partnerId) {
    return stockAccumulatorRepo.findByStockAccumulatorRoleAndAccumulationPartnerId(
        role.toString(), partnerId);
  }

  @Override
  public List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(
      StockAccumulatorRole role, Long partnerId, OperationalStatus status) {
    return stockAccumulatorRepo.findByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(
        role.toString(), partnerId, status.name());
  }

  @Override
  public StockAccumulator getByUserId(Long userId) {
    return stockAccumulatorRepo.findByUserId(userId);
  }
}
