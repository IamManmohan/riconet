package com.rivigo.riconet.core.test.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.Pickup;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created by aditya on 3/5/18. */
public class ApiServiceUtils {

  public static final Long PICKUP_ID = 1L;
  public static final Long CLIENT_ID = 1L;

  public static JsonNode getSampleJsonNode() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree("{\"test-key\":\"test-value\"}");
  }

  public static NotificationDTO getDummyPickupCompleteNotificationDto() {
    Map<String,String> hmap = new HashMap<>();
    hmap.put(ZoomCommunicationFieldNames.CLIENT_ID.name(), CLIENT_ID.toString());
    return NotificationDTO.builder()
        .entityId(PICKUP_ID)
        .metadata(hmap)
        .eventName(EventName.PICKUP_COMPLETION)
        .tsMs(DateTime.now().getMillis())
        .build();
  }

  public static Pickup getDummyPickup() {
    Pickup pickup = new Pickup();
    pickup.setPickupDate(DateTime.now());
    return pickup;
  }

  public static ConsignmentReadOnly getConsignmentWithCnote(String cnote) {
    ConsignmentReadOnly consignment = new ConsignmentReadOnly();
    consignment.setCnote(cnote);
    consignment.setPromisedDeliveryDateTime(DateTime.now().plusDays(RandomUtils.nextInt(2,10)));
    return consignment;
  }
}
