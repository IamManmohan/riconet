package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;

public interface AdministrativeEntityService {

  AdministrativeEntity findParentCluster( Long locationId);
}
