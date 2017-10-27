package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.TaskStatus;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class TaskDetailsDTO {
    private Long taskId;
    private Long tripId;
    private TaskStatus taskStatus;
    private ZoomTripType tripType;
    private TaskType taskType;
    private String deliveryFranchiseCode;
    private String vehicleNo;
    private Boolean isStartLocation;
    private VehiclePreInspectionDTO vehiclePreInspection;
    List<ManifestScanDTO> manifests = new ArrayList<>(0);
    private Map<String, String> locationmap;
    private List<DEPSRecordTaskDetailDTO> depsRecords;
    private CargonetDTO cargonet;
    private List<ReplacedBarcodeMappingScanDTO> replacedBarcodes;
    private DateTime lastUpdatedAt;
    private List<TaskUserDTO> taskUsers;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TaskUserDTO {
        Long id;
        String name;
        TaskStatus status;
    }
}
