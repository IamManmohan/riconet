package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.BfPickupChargesEventName;
import com.rivigo.riconet.core.service.PickupService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BfPickupChargesActionConsumer extends EventConsumer {

  @Autowired private PickupService pickupService;

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(BfPickupChargesEventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    pickupService.deductPickupCharges(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "BfPickupChargesActionConsumer";
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.bfPickupChargesActionError();
  }
}
