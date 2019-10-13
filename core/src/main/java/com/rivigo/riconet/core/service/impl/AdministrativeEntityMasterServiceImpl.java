package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.AdministrativeEntityMasterService;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AdministrativeEntityMasterServiceImpl implements AdministrativeEntityMasterService {

  @Autowired private AdministrativeEntityRepository administrativeEntityRepository;

  @Override
  public List<AdministrativeEntity> findByIdIn(List<Long> ids) {
    return (administrativeEntityRepository.findByIdIn(ids));
  }
}