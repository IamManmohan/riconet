package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.OrganizationType;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.repository.mysql.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    public Organization getById(Long orgId) {
        return organizationRepository.findOne(orgId);
    }

    public List<Organization> getByOrganizationTypeAndOperationalStatus(OrganizationType rivigo, OperationalStatus status) {
        return organizationRepository.findByTypeAndStatus(rivigo, status);
    }
}
