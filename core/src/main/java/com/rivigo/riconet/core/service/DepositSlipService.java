package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.depositslip.DepositSlip;
import java.util.Optional;

public interface DepositSlipService {
  Optional<DepositSlip> findByDepositSlipId(Long depositSlipId);
}
