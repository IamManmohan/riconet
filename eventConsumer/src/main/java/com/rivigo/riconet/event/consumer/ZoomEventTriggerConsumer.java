package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.EventTriggerService;
import de.flapdoodle.embed.process.collections.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 19/4/18. */
@Slf4j
@Component
public class ZoomEventTriggerConsumer extends EventConsumer {

  @Autowired private EventTriggerService eventTriggerService;

  @Override
  public List<EventName> eventNamesToBeConsumed() {
    return Collections.newArrayList(EventName.values());
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    eventTriggerService.processNotification(notificationDTO);
  }
}
