package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.AbstractListConverter;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.AdministrativeEntityMasterService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FeederVendorConverter extends AbstractListConverter<FeederVendorDTO, FeederVendor> {

    @Autowired
    private FeederVendorRepository feederVendorRepository;

    @Autowired private AdministrativeEntityMasterService aeService;

    @Override
    public FeederVendor convertTo(FeederVendorDTO source) {
        FeederVendor feederVendor = null;
        if (source.getId() != null) {
            feederVendor = feederVendorRepository.findById(source.getId());
            if (feederVendor == null) {
                throw new ZoomException(
                        "No feeder vendor with id " + source.getId() + " exists in the database");
            }
        } else {
            feederVendor = new FeederVendor();
        }

        if (source.getVendorName() != null) {
            feederVendor.setVendorName(source.getVendorName());
        }
        if (source.getVendorCode() != null) {
            feederVendor.setVendorCode(source.getVendorCode());
        }
        if (source.getEmail() != null) {
            feederVendor.setEmail(source.getEmail());
        }
        if (source.getPersonName() != null) {
            feederVendor.setPersonName(source.getPersonName());
        }
        if (source.getContactNumber() != null) {
            feederVendor.setContactNumber(source.getContactNumber());
        }
        if (source.getClusterId() != null) {
            feederVendor.setClusterId(source.getClusterId());
        }
        if (source.getVendorType() != null) {
            feederVendor.setVendorType(source.getVendorType());
        }
        if (source.getVendorStatus() != null) {
            feederVendor.setVendorStatus(OperationalStatus.valueOf(source.getVendorStatus()));
        }
        return feederVendor;
    }

    @Override
    public FeederVendorDTO convertFrom(FeederVendor source) {
        FeederVendorDTO feederVendorDTO = new FeederVendorDTO();
        if (source.getId() != null) {
            feederVendorDTO.setId(source.getId());
        }
        if (source.getVendorName() != null) {
            feederVendorDTO.setVendorName(source.getVendorName());
        }
        feederVendorDTO.setContactNumber(source.getContactNumber());
        feederVendorDTO.setVendorCode(source.getVendorCode());
        feederVendorDTO.setPersonName(source.getPersonName());
        feederVendorDTO.setEmail(source.getEmail());
        feederVendorDTO.setClusterId(source.getClusterId());
        if (source.getVendorStatus() != null) {
            feederVendorDTO.setVendorStatus(source.getVendorStatus().toString());
        }
        if (source.getVendorType() != null) {
            feederVendorDTO.setVendorType(source.getVendorType());
        }
        return feederVendorDTO;
    }

    @Override
    public List<FeederVendorDTO> convertListFrom(Iterable<FeederVendor> iterable) {
        if (IterableUtils.isEmpty(iterable)) {
            return Collections.emptyList();
        }

        Set<Long> clusterIdSet = new HashSet<>();
        List<FeederVendorDTO> feederVendorDTOList =
                StreamSupport.stream(iterable.spliterator(), false)
                        .map(this::convertFrom)
                        .map(
                                feederVendorDTO -> {
                                    clusterIdSet.add(feederVendorDTO.getClusterId());
                                    return feederVendorDTO;
                                })
                        .collect(Collectors.toList());
        List<Long> clusterIdList = new ArrayList<>(clusterIdSet);
        List<AdministrativeEntity> administrativeEntityList = aeService.findByIdIn(clusterIdList);
        Map<Long, String> clusterIdToClusterCodeMap =
                administrativeEntityList
                        .stream()
                        .collect(Collectors.toMap(AdministrativeEntity::getId, AdministrativeEntity::getName));

        return feederVendorDTOList
                .stream()
                .map(
                        feederVendorDTO -> {
                            feederVendorDTO.setClusterCode(
                                    clusterIdToClusterCodeMap.get(feederVendorDTO.getClusterId()));
                            return feederVendorDTO;
                        })
                .collect(Collectors.toList());
    }
}
