package com.rivigo.riconet.core.dto.primesync;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

@Getter
@Setter
@ToString
public class PrimeVehicleNodeTrackingDto implements Comparable<PrimeVehicleNodeTrackingDto> {

  private Long id;
  private String nodeType;
  private Long nodeId;
  private String nodeCode;
  private DateTime eta;
  private DateTime etd;
  private DateTime gpsInTimestamp;
  private DateTime gpsOutTimestamp;
  private Integer stopSequence;

  @Override
  public int compareTo(PrimeVehicleNodeTrackingDto dto) {
    return this.stopSequence.compareTo(dto.getStopSequence());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof PrimeVehicleNodeTrackingDto
        && (this.compareTo((PrimeVehicleNodeTrackingDto) obj) == 0);
  }

  @Override
  public int hashCode() {
    return (this.stopSequence.toString()).hashCode();
  }
}
