package com.rivigo.riconet.core.dto.client;

import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientIntegrationRequestDTO extends HiltiRequestDto {
    private ClientConsignmentMetadata metadata;

    public ClientIntegrationRequestDTO(HiltiRequestDto requestDto) {
        this.setReferenceNumber(requestDto.getReferenceNumber());
        this.setJobType(requestDto.getJobType());
        this.setNewStatusCode(requestDto.getNewStatusCode());
        this.setFieldData(requestDto.getFieldData());
    }
}
