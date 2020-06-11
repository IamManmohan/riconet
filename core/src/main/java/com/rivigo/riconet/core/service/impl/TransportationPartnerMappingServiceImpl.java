package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.TransportationPartnerMappingService;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportationPartnerMappingServiceImpl
    implements TransportationPartnerMappingService {

  @Autowired private TransportationPartnerMappingRepository transportationPartnerMappingRepository;

  @Override
  public TransportationPartnerMapping getByDRSId(Long drsId) {
    return transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(
        ZoomTripType.DRS, drsId);
  }

  /**
   * This function is used to get map of drs id to user id based on list of drs Ids.
   *
   * @param drsIds list of drs Ids.
   * @return map of drs id to user id.
   */
  @Override
  public Map<Long, Long> getUserIdByDrsId(List<Long> drsIds) {
    return transportationPartnerMappingRepository
        .findByTransportationTypeAndTransportationIdIn(ZoomTripType.DRS, drsIds)
        .stream()
        .collect(
            Collectors.toMap(
                TransportationPartnerMapping::getTransportationId,
                TransportationPartnerMapping::getUserId));
  }
}
