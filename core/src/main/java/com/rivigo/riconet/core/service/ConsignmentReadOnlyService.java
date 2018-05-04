package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentReadOnlyService {

  ConsignmentReadOnly findByConsignmentById(Long id);
}
