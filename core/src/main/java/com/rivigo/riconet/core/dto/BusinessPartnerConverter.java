package com.rivigo.riconet.core.dto;

import com.rivigo.riconet.core.service.BusinessPartnerService;
import com.rivigo.zoom.common.enums.BusinessPartnerType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.TacticalCreditStatus;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class BusinessPartnerConverter
        extends AbstractListConverter<BusinessPartnerDTO, BusinessPartner> {

    @Autowired
    BusinessPartnerRepository bpRepo;

    @Autowired private BusinessPartnerService businessPartnerService;

    @Override
    public BusinessPartner convertTo(BusinessPartnerDTO source) {
        BusinessPartner bp = null;
        if (source.getId() != null) {
            bp = bpRepo.findOne(source.getId());
        } else {
            bp = new BusinessPartner();
        }
        bp.setCode(source.getCode());
        bp.setName(source.getName());
        if (source.getType() != null) {
            bp.setType(BusinessPartnerType.fromString(source.getType()));
        } else {
            bp.setType(BusinessPartnerType.VOVO);
        }
        bp.setStatus(OperationalStatus.valueOf(source.getStatus()));
        bp.setAvailabilityStatus(source.getAvailabilityStatus());
        bp.setTacticalCreditStatus(
                Optional.ofNullable(source.getTacticalCreditStatus())
                        .orElse(TacticalCreditStatus.DISABLED));
        bp.setTacticalCreditMinimumBalance(
                Optional.ofNullable(source.getTacticalCreditMinimumBalance()).orElse(BigDecimal.ZERO));
        return bp;
    }

    @Override
    public BusinessPartnerDTO convertFrom(BusinessPartner source) {
        return convertFrom(
                source, businessPartnerService.getLocalityDTOListForBusinessPartner(source.getId()));
    }

    private BusinessPartnerDTO convertFrom(
            BusinessPartner source, List<LocalityDTO> localityDTOList) {
        BusinessPartnerDTO bpDto = new BusinessPartnerDTO();
        bpDto.setId(source.getId());
        bpDto.setCode(source.getCode());
        bpDto.setName(source.getName());
        if (source.getType() != null) {
            bpDto.setType(source.getType().toString());
        }
        if (source.getStatus() != null) {
            bpDto.setStatus(source.getStatus().toString());
        }
        bpDto.setAvailabilityStatus(source.getAvailabilityStatus());
        bpDto.setTacticalCreditStatus(source.getTacticalCreditStatus());
        bpDto.setTacticalCreditMinimumBalance(source.getTacticalCreditMinimumBalance());
        Set<String> pincodeSet = new HashSet<>();
        for (LocalityDTO localityDTO : localityDTOList) {
            pincodeSet.add(localityDTO.getPincode());
        }
        bpDto.setLocalityDTOList(localityDTOList);
        bpDto.setPincodeList(new ArrayList<>(pincodeSet));
        return bpDto;
    }

    public Iterable<BusinessPartnerDTO> convertListFrom(Iterable<BusinessPartner> sourceList) {
        return convertListFrom(sourceList, true);
    }

    public Iterable<BusinessPartnerDTO> convertListFrom(
            Iterable<BusinessPartner> sourceList, boolean isLocalityNeeded) {

        if (IterableUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        Map<Long, List<LocalityDTO>> bpIdToLocalityDTOListMap =
                (isLocalityNeeded)
                        ? businessPartnerService.getLocalityDTOListForAllBusinessPartners()
                        : Collections.emptyMap();

        List<BusinessPartnerDTO> bpDtoList = new ArrayList<>();
        for (BusinessPartner bp : sourceList) {
            bpDtoList.add(
                    convertFrom(
                            bp, bpIdToLocalityDTOListMap.getOrDefault(bp.getId(), Collections.emptyList())));
        }
        return bpDtoList;
    }
}

