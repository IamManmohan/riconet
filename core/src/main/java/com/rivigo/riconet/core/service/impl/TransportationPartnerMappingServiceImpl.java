package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.TransportationPartnerMappingService;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportationPartnerMappingServiceImpl implements
    TransportationPartnerMappingService {

  @Autowired
  private TransportationPartnerMappingRepository transportationPartnerMappingRepository;

  @Override
  public TransportationPartnerMapping getByDRSId(Long drsId) {
    return transportationPartnerMappingRepository
        .findByTransportationTypeAndTransportationId(ZoomTripType.DRS, drsId);
  }
}
