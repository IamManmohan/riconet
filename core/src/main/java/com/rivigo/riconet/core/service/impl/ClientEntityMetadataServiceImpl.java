package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.ClientEntityMetadataService;
import com.rivigo.zoom.common.enums.ClientEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ClientEntityMetadata;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.ClientEntityMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientEntityMetadataServiceImpl implements ClientEntityMetadataService {

  @Autowired private ClientEntityMetadataRepository clientEntityMetadataRepository;

  @Autowired private AdministrativeEntityService administrativeEntityService;

  public ClientEntityMetadata getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
      ClientEntityType entityType,
      Long entityId,
      Long clientId,
      Long organizationId,
      OperationalStatus status) {
    return clientEntityMetadataRepository
        .findByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            entityType, entityId, clientId, organizationId, status);
  }

  public ClientEntityMetadata
      getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatusAndUpdatedAtGreaterThan(
          ClientEntityType entityType,
          Long entityId,
          Long clientId,
          Long organizationId,
          OperationalStatus status,
          DateTime lastUpdatedTime) {
    return clientEntityMetadataRepository
        .findByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatusAndLastUpdatedAtGreaterThan(
            entityType, entityId, clientId, organizationId, status, lastUpdatedTime);
  }

  public ClientEntityMetadata getClientClusterMetadata(Consignment consignment) {
    AdministrativeEntity administrativeEntity =
        administrativeEntityService.findParentCluster(consignment.getFromId());

    if (consignment.getOrganizationId() == ConsignmentConstant.RIVIGO_ORGANIZATION_ID) {
      return getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
          ClientEntityType.CLUSTER,
          administrativeEntity.getId(),
          consignment.getClient().getId(),
          ClientEntityMetadata.getDefaultLongValue(),
          OperationalStatus.ACTIVE);
    } else {
      return getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
          ClientEntityType.CLUSTER,
          administrativeEntity.getId(),
          ClientEntityMetadata.getDefaultLongValue(),
          consignment.getOrganizationId(),
          OperationalStatus.ACTIVE);
    }
  }
}
