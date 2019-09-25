package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import java.util.List;

public interface AdministrativeEntityMasterService {

  List<AdministrativeEntity> findByIdIn(List<Long> ids);
}
