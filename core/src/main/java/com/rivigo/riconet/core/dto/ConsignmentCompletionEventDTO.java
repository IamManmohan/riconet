package com.rivigo.riconet.core.dto;

import com.rivigo.zoom.common.dto.client.ClientPincodeMetadataDTO;
import com.rivigo.zoom.common.dto.client.UserClusterMetadataDTO;
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
  private Boolean isRTOCnote;
  private ClientPincodeMetadataDTO clientPincodeMetadataDTO;
  private UserClusterMetadataDTO userClusterMetadataDTO;
}
