package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.depositslip.DepositSlip;

public interface DepositSlipService {
  DepositSlip findByDepositSlipId(String depositSlipId);
}
