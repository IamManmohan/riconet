package com.rivigo.riconet.core.dto.athenagps;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthenaGpsEventDto {

  private Long nodeId; // Just for logging purposes
  private String nodeCode;
  private String sourceId; // Vehicle No
  private Long nodeInAt;
  private Long nodeOutAt;
  private String nodeType; // CLIENT_WAREHOUSE
  private String nodeEventType; // NODE_IN, NODE_OUT, STOPPED
  private String nodeEventStatus; // NEW
  private String dataClient; // RIVIGO_ZOOM
}
