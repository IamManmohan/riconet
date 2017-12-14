package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.LocationType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationService {

    @Autowired
    private LocationRepositoryV2 locationRepository;

    @Autowired
    OrganizationService organizationService;

    public Location getLocationById(Long locationId) {
        return locationRepository.findById(locationId);
    }

    public Location getLocationByCode(String code) {
        return locationRepository.findByCode(code);
    }

    public List<Location> getAllClusterSiblingsOfLocation(String fromLocationCode) {
        Location fromLocation = getLocationByCode(fromLocationCode);
        List<Organization> organization = organizationService.getByOrganizationTypeAndOperationalStatus(OrganizationType.RIVIGO, OperationalStatus.ACTIVE);
        List<Long> orgIds = organization.stream().map(Organization::getId).collect(Collectors.toList());
        return locationRepository.getAllAdministrativeEntitySiblingsOfLocationAndOrganization(fromLocation.getId(), orgIds,LocationType.CLUSTER.name());
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

    public List<Location> getAllRegionSiblingsOfLocation(String fromLocationCode) {
        Location fromLocation = getLocationByCode(fromLocationCode);
        List<Organization> organization = organizationService.getByOrganizationTypeAndOperationalStatus(OrganizationType.RIVIGO, OperationalStatus.ACTIVE);
        List<Long> orgIds = organization.stream().map(Organization::getId).collect(Collectors.toList());
        return locationRepository.getAllAdministrativeEntitySiblingsOfLocationAndOrganization(fromLocation.getId(), orgIds,LocationType.REGION.name());
    }

    public Map<Long,Location> getLocationMap(){
        return locationRepository.findByStatus(OperationalStatus.ACTIVE.name())
                .stream()
                .collect(Collectors.toMap(Location::getId, Function.identity()));
    }
}