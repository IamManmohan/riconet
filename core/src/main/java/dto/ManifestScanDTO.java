package dto;

import com.rivigo.zoom.common.enums.ManifestStatus;
import dto.ConsignmentScanDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ManifestScanDTO {
    Long id;
    String manifestName;
    String fromLocation;
    String toLocation;
    ManifestStatus status;
    Integer totalBoxes;
    //total scanned as per current context : Eg. while loading/unloading
    Integer totalBoxesScanned;
    List<ConsignmentScanDTO> consignmentsList;
}

