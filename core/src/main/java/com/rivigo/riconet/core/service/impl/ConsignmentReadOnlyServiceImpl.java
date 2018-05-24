package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.repository.mysql.ConsignmentReadOnlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsignmentReadOnlyServiceImpl implements ConsignmentReadOnlyService {

  @Autowired private ConsignmentReadOnlyRepository consignmentRepo;

  @Override
  public ConsignmentReadOnly findByConsignmentById(Long id) {
    return consignmentRepo.findOne(id);
  }
}
