package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import org.springframework.stereotype.Service;

@Service
public interface DepositSlipService {
  DepositSlip findByDepositSlipNumber(String depositSlipNumber);
}
