package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdministrativeEntityServiceImpl implements AdministrativeEntityService {

  @Autowired private AdministrativeEntityRepository administrativeEntityRepository;

  @Override
  public AdministrativeEntity findParentCluster(Long locationId) {
    return administrativeEntityRepository.findParentCluster(locationId);
  }
}
