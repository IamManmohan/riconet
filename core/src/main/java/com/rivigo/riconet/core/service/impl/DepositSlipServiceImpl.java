package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.DepositSlipService;
import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import com.rivigo.zoom.common.repository.mysql.depositslip.DepositSlipRepository;
import java.util.Optional;

public class DepositSlipServiceImpl implements DepositSlipService {

  private final DepositSlipRepository depositSlipRepository;

  public DepositSlipServiceImpl(DepositSlipRepository depositSlipRepository) {
    this.depositSlipRepository = depositSlipRepository;
  }

  @Override
  public DepositSlip findByDepositSlipNumber(String depositSlipNumber) {
    Optional<DepositSlip> a = depositSlipRepository.findByDepositSlipNumber(depositSlipNumber);
    return a.orElse(null);
  }
}
