package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplacedBarcodeMappingScanDTO {
    private String barcode;
    private String cnote;
    private String oldBarcode;
}
