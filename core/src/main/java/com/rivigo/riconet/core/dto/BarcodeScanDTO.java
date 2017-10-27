package com.rivigo.riconet.core.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarcodeScanDTO {
    String barcode;
    Boolean isScanned = false;
    Long scannedBy;
}