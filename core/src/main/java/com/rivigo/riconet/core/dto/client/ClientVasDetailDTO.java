package com.rivigo.riconet.core.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rivigo.zoom.common.enums.ClientVasType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ashfakh on 13/09/18.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clientVasType")
@JsonSubTypes({@JsonSubTypes.Type(value = ClientCodDodDTO.class, name = "COD_DOD")})
public class ClientVasDetailDTO {
    private Long id;
    private Long clientId;
    private String status;
    private Boolean financeActivated;
    private ClientVasType clientVasType;
    private Long spotConsignmentId;
}
