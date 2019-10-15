package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentReadOnlyService {

  Optional<ConsignmentReadOnly> findConsignmentById(Long id);

  List<ConsignmentReadOnly> findByPickupId(Long pickupId);
}
