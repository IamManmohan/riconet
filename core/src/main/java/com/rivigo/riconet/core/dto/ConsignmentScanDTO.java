package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.CPBCategory;
import com.rivigo.zoom.common.enums.ServiceType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConsignmentScanDTO implements Comparable<ConsignmentScanDTO>{
    String cnote;
    String fromLocation;
    String toLocation;
    String toLocationName;
    String clientCode;
    ServiceType clientType;
    Integer totalBoxes;
    Integer totalBoxesScanned;
    List<BarcodeScanDTO> barcodeList;
    Long tripId;
    ZoomTripType tripType;
    CPBCategory cpbCategory = null;
    boolean arrived = true;
    boolean complete = true;
    boolean qcDone = true;
    boolean deps = false;
    boolean mandatory = false;

    @Override
    public int compareTo(ConsignmentScanDTO c) {
        if (this.cpbCategory == null && c.cpbCategory == null){
            return 0;
        }
        else if (this.cpbCategory == null){
            return -1;
        }
        else if (c.cpbCategory == null){
            return 1;
        }
        return this.cpbCategory.compareTo(c.cpbCategory);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj!=null) {
            if (obj.getClass().equals(this.getClass())) {
                if (this == obj) {
                    return true;
                }
                return this.equals(obj);
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 1;
    }

}
