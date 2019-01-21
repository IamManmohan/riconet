package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.PickupRunSheet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface PRSService {

  Map<Long, PickupRunSheet> getPrsMapByPRSIdIn(List<Long> prsTripIdList);

  PickupRunSheet getPRSById(Long prsId);
}
