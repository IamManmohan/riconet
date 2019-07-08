package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.BusinessPartnerConverter;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.service.BusinessPartnerService;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.pickupautoassignment.AutoAssignmentEntityMappingRepository;
import com.rivigo.zoom.common.utils.ZoomUtilFunctions;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class BusinessPartnerServiceImpl implements BusinessPartnerService {

    @Autowired private BusinessPartnerRepository businessPartnerRepository;

    @Autowired private BusinessPartnerConverter businessPartnerConverter;

    @Autowired private AutoAssignmentEntityMappingRepository autoAssignmentEntityMappingRepository;

    private BusinessPartner addPartner(BusinessPartner partner) {
        try {
            return businessPartnerRepository.save(partner);
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new ZoomException("Partner already exists");
        }
    }

    @Override
    public BusinessPartnerDTO addBusinessPartner(BusinessPartnerDTO businessPartnerDTO) {

        try {
            if (CollectionUtils.isEmpty(businessPartnerDTO.getLocalityDTOList())) {
                throw new ZoomException(
                        "Business partner can not be added serving no localities BP Code: "
                                + businessPartnerDTO.getCode());
            }
            validateBusinessPartner(businessPartnerDTO);
            BusinessPartner savedBusinessPartner =
                    addPartner(businessPartnerConverter.convertTo(businessPartnerDTO));
            businessPartnerDTO.setId(savedBusinessPartner.getId());

            return businessPartnerConverter.convertFrom(savedBusinessPartner);

        } catch (PersistenceException pe) {
            log.error("Error occurred in addBusinessPartner {}", pe.getStackTrace());
            throw new ZoomException("Partner already exists " + pe.getMessage());
        }
    }

    public void validateBusinessPartner(@NonNull BusinessPartnerDTO businessPartnerDTO) {
        // validate Tactical Credit Minimum Balance
        Optional.ofNullable(businessPartnerDTO)
                .map(BusinessPartnerDTO::getTacticalCreditMinimumBalance)
                .filter(v -> ZoomUtilFunctions.compareBigDecimals(v, BigDecimal.ZERO) < 0)
                .ifPresent(
                        v -> {
                            log.error("Tactical Credit Minimum Balance can not be negative. minBalance :{} ", v);
                            throw new ZoomException("Tactical Credit Minimum Balance can not be negative.");
                        });
    }
}

