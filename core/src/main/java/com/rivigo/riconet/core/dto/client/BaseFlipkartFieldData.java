package com.rivigo.riconet.core.dto.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rivigo.riconet.core.dto.hilti.BaseHiltiFieldData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BaseFlipkartFieldData extends BaseHiltiFieldData{
    private List<String> barcodes;

    public BaseFlipkartFieldData(List<String> barcodes)  {
        this.barcodes = barcodes;
    }
}
