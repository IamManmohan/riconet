package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import org.springframework.stereotype.Service;

@Service
public interface TransportationPartnerMappingService {

  TransportationPartnerMapping getByDRSId(Long drsId);
}
