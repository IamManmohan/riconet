package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.repository.mysql.ConsignmentReadOnlyRepository;
import java.util.List;
import java.util.Optional;

import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsignmentReadOnlyServiceImpl implements ConsignmentReadOnlyService {

  @Autowired private ConsignmentReadOnlyRepository consignmentRepo;

  @Override
  public Optional<ConsignmentReadOnly> findConsignmentById(Long id) {
    return Optional.ofNullable(consignmentRepo.findOne(id));
  }

  @Override
  public ConsignmentReadOnly findRequiredById(Long id) {
    return findConsignmentById(id)
        .orElseThrow(() -> new ZoomException("No consignment found with id: %s", id));
  }

  @Override
  public List<ConsignmentReadOnly> findByPickupId(Long pickupId) {
    return consignmentRepo.findByPickupIdAndIsActive(pickupId, 1);
  }
}
