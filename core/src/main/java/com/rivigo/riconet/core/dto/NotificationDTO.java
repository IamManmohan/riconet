package com.rivigo.riconet.core.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Created by rohith on 21/2/18. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDTO {

  private String eventName;

  private Long entityId;

  private String entityName;

  /** eventGUID is unique id for a group of events for e.g. CN_{consignment_id} */
  private String eventGUID;

  /** time at which this event happened created_at or last_updated_at time in db tables */
  private Long tsMs;

  /** eventUID is unique id for an event eventUID = eventName_entityId_tsMs */
  private String eventUID;

  private Map<String, String> metadata = new HashMap<>();

  /**
   * list of conditions for an event these conditions help in deciding template for subscribers in
   * broker
   */
  private List<String> conditions;

  public static NotificationDTO copy(NotificationDTO input) {
    return NotificationDTO.builder()
        .eventUID(input.getEventUID())
        .eventName(input.getEventName())
        .eventGUID(input.getEventGUID())
        .metadata(input.getMetadata())
        .tsMs(input.getTsMs())
        .conditions(input.getConditions())
        .entityId(input.getEntityId())
        .entityName(input.getEntityName())
        .build();
  }
}
