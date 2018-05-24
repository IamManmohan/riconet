package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentReadOnlyService {

  ConsignmentReadOnly findConsignmentById(Long id);

  List<ConsignmentReadOnly> findConsignmentByPickupId(Long pickupId);
}
