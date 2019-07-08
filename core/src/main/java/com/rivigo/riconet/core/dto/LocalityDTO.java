package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.OperationalStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocalityDTO {

    private Long id;
    private String code;
    private OperationalStatus status;
    private Double latitude;
    private Double longitude;
    private String geofencing;
    private String pincode;
    private Long pincodeId;
    private String localityName;
}
