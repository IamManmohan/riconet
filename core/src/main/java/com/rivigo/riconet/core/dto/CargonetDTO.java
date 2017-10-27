package com.rivigo.riconet.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CargonetDTO {
    boolean unloadImageUploaded = false;
    List<TripAssetDTO> cargonetDetails;
}
