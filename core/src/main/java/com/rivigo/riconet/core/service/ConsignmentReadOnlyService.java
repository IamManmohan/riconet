package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentReadOnlyService {

  Optional<ConsignmentReadOnly> findConsignmentById(Long id);

  ConsignmentReadOnly findRequiredById(Long id);

  List<ConsignmentReadOnly> findByPickupId(Long pickupId);

  Map<Long, String> getCnIdToCnoteMap(List<Long> cnIds);

  /**
   * This function is used to fetch map of consignment id to consignment read only based on list of
   * consignment ids.
   *
   * @param cnIds list of consignment ids.
   * @return map of consignment id to consignment read only.
   */
  Map<Long, ConsignmentReadOnly> getConsignmentMap(List<Long> cnIds);
}
