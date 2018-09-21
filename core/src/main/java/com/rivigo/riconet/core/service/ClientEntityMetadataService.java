package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.ClientEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ClientEntityMetadata;
import com.rivigo.zoom.common.model.Consignment;

public interface ClientEntityMetadataService {

  ClientEntityMetadata getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
      ClientEntityType entityType,
      Long entityId,
      Long clientId,
      Long organizationId,
      OperationalStatus status);

  ClientEntityMetadata getClientClusterMetadata(Consignment consignment);
}
