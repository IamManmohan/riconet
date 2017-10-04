package dto;

import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.DEPSRecord;
import com.rivigo.zoom.common.model.OATaskAssignment;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by nischey on 23/8/17.
 */
@Getter
@Setter
@Builder
public class DEPSCreationContext {
    TaskDetailsDTO taskDetailsDTO;
    OATaskAssignment task;
    List<Box> boxes;
    Map<String, Box> boxesMap;
    List<Long> consignmentIds;
    List<DEPSRecord> depsRecords;
    Map<String, DEPSRecord> depsRecordMap;
    List<Consignment> consignments;
    Map<Long, Consignment> idToConsignmentMap;
    Map<Long, List<ConsignmentSchedule>> cnToScheduleMap;

    List<DEPSRecordDetailDTO> depsRecordDetailDTOList;
    User sessionUser;
    Location location;
    boolean isInStock;
}
