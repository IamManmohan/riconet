package com.rivigo.riconet.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.DEPSRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by mohdimran on 11/9/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DEPSNotificationContext {
    Map<Long, Consignment> consignmentIdToConsignmentMap;
    Map<Long, List<ConsignmentSchedule>> consignmentIdToScheduleMap;
    List<DEPSRecord> newDEPSRecordList;

    public DEPSNotificationContext(DEPSCreationContext context, List<DEPSRecord> depsRecordList) {
        this.consignmentIdToConsignmentMap = context.getIdToConsignmentMap();
        this.consignmentIdToScheduleMap = context.getCnToScheduleMap();
        this.newDEPSRecordList = depsRecordList;
    }
}
