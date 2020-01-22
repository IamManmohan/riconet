package com.rivigo.riconet.core.dto.primesync;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

@Getter
@Setter
@ToString
public class PrimeEventDto {

  private Integer clientId;
  private String clientCode;
  private String vehicleNumber;
  private String clientRouteName;
  private Long nodeId;
  private String nodeType;
  private String nodeCode;
  private Long journeyId;
  private String journeyType;
  private List<PrimeVehicleNodeTrackingDto> cwhTrackingList;
  private DateTime eventTimestamp;
  private String primeEventType;
}
