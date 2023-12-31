package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.ConsignmentSchedule;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface ConsignmentScheduleService {

  Map<Long, List<ConsignmentSchedule>> getActivePlansMapByIds(Collection<Long> consignmentIds);

  List<ConsignmentSchedule> getActivePlan(Long consignmentId);
}
