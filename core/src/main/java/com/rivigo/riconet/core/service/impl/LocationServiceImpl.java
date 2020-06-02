package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.zoom.common.enums.LocationType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

  @Autowired private LocationRepositoryV2 locationRepository;

  @Autowired private OrganizationService organizationService;

  public Location getLocationById(Long locationId) {
    return locationRepository.findById(locationId);
  }

  public Location getLocationByCode(String code) {
    return locationRepository.findByCode(code);
  }

  public List<Location> getAllClusterSiblingsOfLocation(String fromLocationCode) {
    Location fromLocation = getLocationByCode(fromLocationCode);
    List<Organization> organization =
        organizationService.getByOrganizationTypeAndOperationalStatus(
            OrganizationType.RIVIGO, OperationalStatus.ACTIVE);
    List<Long> orgIds = organization.stream().map(Organization::getId).collect(Collectors.toList());
    return locationRepository.getAllAdministrativeEntitySiblingsOfLocationAndOrganization(
        fromLocation.getId(), orgIds, LocationType.CLUSTER.name());
  }

  public Location getPcOrReportingPc(Location l) {
    if (l == null) {
      return null;
    }
    if (l.getLocationType() == LocationType.PROCESSING_CENTER) {
      return l;
    } else {
      return locationRepository.getReportingPc(l.getId());
    }
  }

  public Map<Long, Location> getLocationMap() {
    return locationRepository
        .findByStatus(OperationalStatus.ACTIVE.name())
        .stream()
        .collect(Collectors.toMap(Location::getId, Function.identity()));
  }
}
