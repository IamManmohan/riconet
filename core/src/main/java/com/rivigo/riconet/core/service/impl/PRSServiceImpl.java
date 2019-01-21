package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.PRSService;
import com.rivigo.zoom.common.model.PickupRunSheet;
import com.rivigo.zoom.common.repository.mysql.PRSRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PRSServiceImpl implements PRSService {

  @Autowired private PRSRepository prsRepository;

  @Override
  public Map<Long, PickupRunSheet> getPrsMapByPRSIdIn(List<Long> prsTripIdList) {
    return ((List<PickupRunSheet>) prsRepository.findAll(prsTripIdList))
        .stream()
        .collect(Collectors.toMap(PickupRunSheet::getId, Function.identity()));
  }

  @Override
  public PickupRunSheet getPRSById(Long prsId) {
    return prsRepository.findOne(prsId);
  }
}
