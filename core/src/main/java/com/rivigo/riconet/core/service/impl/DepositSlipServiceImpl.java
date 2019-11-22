package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.DepositSlipService;
import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import com.rivigo.zoom.common.repository.mysql.depositslip.DepositSlipRepository;
import org.springframework.stereotype.Service;

@Service
public class DepositSlipServiceImpl implements DepositSlipService {

  private final DepositSlipRepository depositSlipRepository;

  public DepositSlipServiceImpl(DepositSlipRepository depositSlipRepository) {
    this.depositSlipRepository = depositSlipRepository;
  }

  @Override
  public DepositSlip findByDepositSlipNumber(String depositSlipNumber) {
    return depositSlipRepository.findByDepositSlipNumber(depositSlipNumber).orElse(null);
  }
}
