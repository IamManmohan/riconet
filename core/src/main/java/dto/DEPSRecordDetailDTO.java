package dto;

import com.rivigo.zoom.common.enums.DEPSStatus;
import com.rivigo.zoom.common.enums.DEPSTaskType;
import com.rivigo.zoom.common.enums.DEPSType;
import com.rivigo.zoom.common.enums.UnConnectedBoxStatus;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.DEPSRecord;
import com.rivigo.zoom.common.model.neo4j.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Created by hitesh on 8/31/16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DEPSRecordDetailDTO {
    private Long id;
    private String barcode;
    private String cNote;
    private Long taskId;
    private Long boxId;
    private Long clientId;
    private String clientName;
    private String clientCode;
    private String locationCode;
    private DEPSType depsType;
    private DEPSStatus depsStatus;
    private Long reportedById;
    private String reportedByName;
    private String reportedByMobileNo;
    private Long inchargeId;
    private String inchargeName;
    private Long tripId;
    private ZoomTripType tripType;
    private DateTime reportedTime;
    private String fromLocation;
    private String toLocation;
    private String vehicleNumber;
    private String viaLocations;
    private UnConnectedBoxStatus unConnectedBoxStatus;
    private String newBarcode;
    private DEPSTaskType depsTaskType;
    private String newCnote;
    private UnConnectedBoxDetailDTO unconnectedBoxDetail;
    private Location inboundLocation;
    private String resolvingDepsLocationCode;
    private Long parentDEPSId;
    private String routeName;
    private String reOpenTicketCommets;


    public DEPSRecordDetailDTO(DEPSRecord depsRecord) {
        this.id = depsRecord.getId();
        this.barcode = depsRecord.getBarcode();
        this.cNote = depsRecord.getCNote();
        this.newCnote = depsRecord.getNewCnote();
        this.boxId = depsRecord.getBoxId();
        this.clientId = depsRecord.getClientId();
        this.depsType = depsRecord.getDepsType();
        this.depsStatus = depsRecord.getDepsStatus();
        this.reportedById = depsRecord.getReportedById();
        this.inchargeId = depsRecord.getInchargeId();
        this.tripId = depsRecord.getTripId();
        this.reportedTime = depsRecord.getCreatedAt();
        this.tripType = depsRecord.getTripType();
        this.unConnectedBoxStatus = depsRecord.getUnConnectedBoxStatus();
        this.vehicleNumber = depsRecord.getVehicleNumber();
        this.inchargeName = depsRecord.getInchargeName();
        this.reportedByName = depsRecord.getReportedByName();
        this.taskId = depsRecord.getTaskId();
        this.newBarcode = depsRecord.getNewBarcode();
        this.depsTaskType = depsRecord.getDepsTaskType();
        this.parentDEPSId = depsRecord.getParentDEPSId();
    }

    public DEPSRecordDetailDTO(DEPSRecord depsRecord,
                               String clientName, String clientCode, String locationCode) {
        this(depsRecord);
        this.clientName = clientName;
        this.clientCode = clientCode;
        this.locationCode = locationCode;
    }

    public DEPSRecordDetailDTO(DEPSRecord depsRecord,
                               String clientName, String clientCode, String locationCode, String resolvingDepsLocationCode) {
        this(depsRecord,clientName,clientCode,locationCode);
        this.resolvingDepsLocationCode = resolvingDepsLocationCode;
    }

}
