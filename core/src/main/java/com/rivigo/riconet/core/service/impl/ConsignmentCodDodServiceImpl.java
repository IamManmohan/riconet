package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ConsignmentCodDodService;
import com.rivigo.zoom.common.model.ConsignmentCodDod;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCodDodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ConsignmentCodDodServiceImpl implements ConsignmentCodDodService {

  @Autowired
  private ConsignmentCodDodRepository consignmentCodDodRepository;

  @Override
  public ConsignmentCodDod getActiveCodDod(Long consignmentId) {
    return consignmentCodDodRepository.findByConsignmentIdAndIsActive(consignmentId,true);
  }
}
