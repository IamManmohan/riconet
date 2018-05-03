package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.repository.mysql.OrganizationRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

  @Autowired
  private OrganizationRepository organizationRepository;

  @Override
  public Organization getById(Long orgId) {
    return organizationRepository.findOne(orgId);
  }

  @Override
  public List<Organization> getByOrganizationTypeAndOperationalStatus(OrganizationType rivigo,
      OperationalStatus status) {
    return organizationRepository.findByTypeAndStatus(rivigo, status);
  }
}
