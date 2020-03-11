package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.neo4j.Location;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LocationService {

  Location getLocationById(Long locationId);

  Location getLocationByCode(String code);

  List<Location> getAllClusterSiblingsOfLocation(String fromLocationCode);

  Location getPcOrReportingPc(Location l);

  Map<Long, Location> getLocationMap();
}
