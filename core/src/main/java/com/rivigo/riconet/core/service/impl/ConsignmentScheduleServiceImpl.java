package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.repository.mysql.ConsignmentScheduleRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 25/9/17. */
@Slf4j
@Service
public class ConsignmentScheduleServiceImpl implements ConsignmentScheduleService {

  @Autowired private ConsignmentScheduleRepository consignmentScheduleRepository;

  @Override
  public Map<Long, List<ConsignmentSchedule>> getActivePlansMapByIds(Collection<Long> consignmentIds) {
    List<ConsignmentSchedule> schedules =
        consignmentScheduleRepository.findByConsignmentIdInAndIsActive(new ArrayList<>(consignmentIds), 1);
    Collections.sort(schedules);
    Map<Long, List<ConsignmentSchedule>> retMap = new HashMap<>();
    for (ConsignmentSchedule schedule : schedules) {
      List<ConsignmentSchedule> scheduleList = retMap.computeIfAbsent(schedule.getConsignmentId(), k -> new ArrayList<>());
      scheduleList.add(schedule);
    }
    return retMap;
  }

  @Override
  public List<ConsignmentSchedule> getActivePlan(Long consignmentId) {
    List<ConsignmentSchedule> csList = consignmentScheduleRepository.findByConsignmentIdAndIsActive(consignmentId, 1);
    Collections.sort(csList);
    return csList;
  }
}
