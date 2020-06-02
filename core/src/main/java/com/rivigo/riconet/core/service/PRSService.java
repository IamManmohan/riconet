package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.PickupRunSheet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface PRSService {

  Map<Long, PickupRunSheet> getPrsMapByPRSIdIn(List<Long> prsTripIdList);

  /**
   * This function is used to list of pickup run sheets bu ids.
   *
   * @param prsIds list of PRS ids.
   * @return list of pickup run sheet.
   */
  List<PickupRunSheet> getPickupRunSheets(List<Long> prsIds);
}
