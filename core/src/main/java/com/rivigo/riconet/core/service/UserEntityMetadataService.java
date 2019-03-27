package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.LocationEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.UserEntityType;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.UserEntityMetadata;

public interface UserEntityMetadataService {

  UserEntityMetadata
      getByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
          LocationEntityType locationEntityType,
          Long locationEntityId,
          UserEntityType userEntityType,
          Long userEntityId,
          OperationalStatus status);

  UserEntityMetadata getUserClusterMetadata(Consignment consignment);
}
