package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface TransportationPartnerMappingService {

  TransportationPartnerMapping getByDRSId(Long drsId);

  /**
   * This function is used to get map of drs id to user id based on list of drs Ids.
   *
   * @param drsIds list of drs Ids.
   * @return map of drs id to user id.
   */
  Map<Long, Long> getUserIdByDrsId(List<Long> drsIds);
}
