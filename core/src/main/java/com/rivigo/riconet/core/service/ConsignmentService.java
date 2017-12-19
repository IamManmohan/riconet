package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.repository.mysql.ConsignmentHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsignmentService {

    @Autowired
    ConsignmentRepository consignmentRepo;

    @Autowired
    ConsignmentHistoryRepository historyRepo;

    public Map<Long,ConsignmentHistory> getLastScanByCnIdIn(List<Long> cnIds, List<String> statusList){
        List<ConsignmentHistory> historyList=historyRepo.
                findTop1ByConsignmentIdInAndStatusInGroupByConsignmentId(cnIds,statusList);
        return historyList.stream().collect(Collectors.toMap(ConsignmentHistory::getConsignmentId, c->c));
    }

    public ConsignmentHistory getLastScanByCnId(Long cnId, List<String> statusList){
        List<ConsignmentHistory> historyList=historyRepo.
                findTop1ByConsignmentIdInAndStatusInGroupByConsignmentId(
                        Arrays.asList(cnId),statusList);
        return historyList.isEmpty()?null:historyList.get(0);
    }

    public Integer getOriginalNumberOfBoxesByCnote(String cnote){
        return consignmentRepo.getOriginalNumOfBoxes(cnote);
    }

    public List<Consignment> findByIdInAndStatusNotInAndDeliveryHandoverIsNull(List<Long> consignmentIdList, List<ConsignmentStatus> statusList){
        return  consignmentRepo.findByIdInAndStatusNotInAndDeliveryHandoverIsNull(consignmentIdList,statusList);
    }

    public List<Consignment> getConsignmentsByIds(List<Long> consignmentIds) {
        if (consignmentIds != null && !consignmentIds.isEmpty()) {
            return consignmentRepo.findByIdIn(consignmentIds);
        }
        return new ArrayList<>();
    }

    public String getCnoteByIdAndIsActive(Long id) {
        return consignmentRepo.getCnoteByIdAndIsActive(id, Boolean.TRUE);
    }
}
