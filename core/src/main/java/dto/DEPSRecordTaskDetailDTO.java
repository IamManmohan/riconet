package dto;

import com.rivigo.zoom.common.enums.DEPSStatus;
import com.rivigo.zoom.common.enums.DEPSType;
import com.rivigo.zoom.common.model.DEPSRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by hitesh on 8/31/16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DEPSRecordTaskDetailDTO {

    private Long id;
    private String barcode;
    private String newBarcode;
    private Long consignmentId;
    private String cNote;
    private Long taskId;
    private Long boxId;
    private DEPSType depsType;
    private DEPSStatus depsStatus;
    private DEPSType initialDEPSType;
    private Long createdBy;

    public DEPSRecordTaskDetailDTO(DEPSRecord depsRecord) {
        this.id = depsRecord.getId();
        this.newBarcode = depsRecord.getNewBarcode();
        this.barcode = depsRecord.getBarcode();
        this.cNote = depsRecord.getCNote();
        this.boxId = depsRecord.getBoxId();
        this.depsType = depsRecord.getDepsType();
        this.depsStatus = depsRecord.getDepsStatus();
        this.taskId = depsRecord.getTaskId();
        this.initialDEPSType = depsRecord.getInitialDepsType();
        this.consignmentId = depsRecord.getConsignmentId();
        this.createdBy = depsRecord.getCreatedById();
    }
}
