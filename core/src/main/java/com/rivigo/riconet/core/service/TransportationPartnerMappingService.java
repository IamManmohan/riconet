package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface TransportationPartnerMappingService {

  TransportationPartnerMapping getByDRSId(Long drsId);

  Map<Long, Long> getUserIdByDrsId(List<Long> drsIds);
}
