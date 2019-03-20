package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.UserEntityMetadataService;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.LocationEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.UserEntityType;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.UserEntityMetadata;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.UserEntityMetadataRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEntityMetadataServiceImpl implements UserEntityMetadataService {

  @Autowired private UserEntityMetadataRepository userEntityMetadataRepository;

  @Autowired private AdministrativeEntityService administrativeEntityService;

  public UserEntityMetadata
      getByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
          LocationEntityType locationEntityType,
          Long locationEntityId,
          UserEntityType userEntityType,
          Long userEntityId,
          OperationalStatus status) {
    return userEntityMetadataRepository
        .findByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
            locationEntityType, locationEntityId, userEntityType, userEntityId, status);
  }

  public UserEntityMetadata getUserClusterMetadata(Consignment consignment) {
    AdministrativeEntity administrativeEntity =
        administrativeEntityService.findParentCluster(consignment.getFromId());
    if (consignment.getOrganizationId().equals(ConsignmentConstant.RIVIGO_ORGANIZATION_ID)) {
      if (CnoteType.RETAIL.equals(consignment.getCnoteType())) {
        Optional<Long> rpId = getRpIdForConsignment(consignment);
        if (!rpId.isPresent()) {
          log.info("No RP exists for this consignment : {}", consignment.getCnote());
          return null;
        }
        return getByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
            LocationEntityType.CLUSTER,
            administrativeEntity.getId(),
            UserEntityType.RP,
            rpId.get(),
            OperationalStatus.ACTIVE);
      }
      log.info(
          "Returning Client Cluster metadata for Client  {}",
          consignment.getClient().getClientCode());
      return getByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
          LocationEntityType.CLUSTER,
          administrativeEntity.getId(),
          UserEntityType.CLIENT,
          consignment.getClient().getId(),
          OperationalStatus.ACTIVE);
    } else {
      log.info(
          "Returning Organization Cluster metadata for Organization id  {}",
          consignment.getOrganizationId());
      return getByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
          LocationEntityType.CLUSTER,
          administrativeEntity.getId(),
          UserEntityType.ORGANIZATION,
          consignment.getOrganizationId(),
          OperationalStatus.ACTIVE);
    }
  }

  private Optional<Long> getRpIdForConsignment(Consignment consignment) {
    if (consignment.getPrs() == null) {
      log.info("No Pickup or this CN : {}", consignment.getCnote());
      return Optional.empty();
    }
    if (consignment.getPrs().getBusinessPartner() == null) {
      log.info("No Pickup BP for this CN : {}", consignment.getCnote());
      return Optional.empty();
    }
    log.info(
        "Returning RP metadata for RP ID {}", consignment.getPrs().getBusinessPartner().getId());
    return Optional.ofNullable(consignment.getPrs().getBusinessPartner().getId());
  }
}
