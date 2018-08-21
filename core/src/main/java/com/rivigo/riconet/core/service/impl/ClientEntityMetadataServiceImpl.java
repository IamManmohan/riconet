package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.service.ClientEntityMetadataService;
import com.rivigo.zoom.common.enums.ClientEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ClientEntityMetadata;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.ClientEntityMetadataRepository;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientEntityMetadataServiceImpl implements ClientEntityMetadataService {

  @Autowired private ClientEntityMetadataRepository clientEntityMetadataRepository;

  @Autowired private AdministrativeEntityRepository administrativeEntityRepository;

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

  public ClientEntityMetadata getClientClusterMetadata(Consignment consignment) {
    AdministrativeEntity administrativeEntity =
        administrativeEntityRepository.findParentCluster(consignment.getFromId());

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
