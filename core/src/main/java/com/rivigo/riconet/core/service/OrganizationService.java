package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface OrganizationService {

  Organization getById(Long orgId);

  List<Organization> getByOrganizationTypeAndOperationalStatus(
      OrganizationType rivigo, OperationalStatus status);
}
