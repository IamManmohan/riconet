package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.DepositSlipService;
import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import com.rivigo.zoom.common.repository.mysql.depositslip.DepositSlipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepositSlipServiceImpl implements DepositSlipService {

  private final DepositSlipRepository depositSlipRepository;

  @Override
  public DepositSlip findByDepositSlipId(String depositSlipId) {
    return depositSlipRepository.findById(Long.valueOf(depositSlipId)).orElse(null);
  }
}
