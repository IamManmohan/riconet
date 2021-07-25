package com.rivigo.riconet.core.dto.athenagps;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * AthenaGpsEventDto contains data coming from Athena for both Node Events & Gps Events. It has
 * union of properties of both the events, will bifurcate them accordingly by EVENT_TYPE property
 */
@Getter
@Setter
@ToString
public class AthenaGpsEventDto {
  private String sourceId; // Vehicle No
  private String eventType; // NODE or GPS

  private Long nodeId; // Just for logging purposes
  private String nodeCode;
  private Long nodeInAt;
  private Long nodeOutAt;
  private String nodeType; // CLIENT_WAREHOUSE
  private String nodeEventType; // NODE_IN, NODE_OUT, STOPPED
  private String nodeEventStatus; // NEW
  private String dataClient; // RIVIGO_ZOOM

  private Double latitude;
  private Double longitude;
  private Long gpsTimestamp;
  private String gpsEventType; // GPS_LAT_LONG_UPDATE
}
