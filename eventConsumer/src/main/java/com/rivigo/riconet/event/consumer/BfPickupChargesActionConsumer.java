package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.PickupService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BfPickupChargesActionConsumer extends EventConsumer {

  @Autowired
  private PickupService pickupService;

  @Override
  public List<EventName> eventNamesToBeConsumed(){
    return Arrays.asList(EventName.CN_COMPLETION_ALL_INSTANCES,EventName.CN_DELETED,EventName.PICKUP_COMPLETION);
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    pickupService.deductPickupCharges(notificationDTO);
  }
}
