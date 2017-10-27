package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.repository.mysql.ConsignmentReadOnlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsignmentReadOnlyService {

    @Autowired
    ConsignmentReadOnlyRepository consignmentRepo;

    public ConsignmentReadOnly findByConsignmentById(Long id) {
        return consignmentRepo.findOne(id);
    }
}
