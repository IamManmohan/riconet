package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.dto.client.ClientClusterMetadataDTO;
import com.rivigo.zoom.common.dto.client.ClientPincodeMetadataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsignmentCompletionEventDTO {

  private String cnote;
  private Long consignmentId;
  private ClientPincodeMetadataDTO clientPincodeMetadataDTO;
  private ClientClusterMetadataDTO clientClusterMetadataDTO;
}