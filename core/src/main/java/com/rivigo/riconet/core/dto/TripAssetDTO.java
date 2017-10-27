package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.enums.AssetPositionType;
import com.rivigo.zoom.common.enums.AssetType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripAssetDTO {
    private Long id;
    private Long entityId;
    private String fromLocation;
    private String toLocation;
    private CargonetEntityType entityType;
    private String barcode;
    private boolean available = false;
    private Long reasonId;
    private AssetType assetType;
    private AssetPositionType assetPositionType;
    private boolean excess = false;
}
