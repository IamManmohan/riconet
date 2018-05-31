package com.rivigo.riconet.event.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.PickupService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class EventConsumer extends ConsumerModel {

  private ObjectMapper objectMapper;

  public abstract List<EventName> eventNamesToBeConsumed();

  public abstract void doAction(NotificationDTO notificationDTO);

  @Override
  public String processMessage(String str) {
    log.info("Processing message in BfPickupChargesActionConsumer {}", str);
    NotificationDTO notificationDTO = null;
    try {
      notificationDTO = objectMapper.readValue(str, NotificationDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", str, ex);
      return str;
    }
    log.debug("NotificationDTO {}", notificationDTO);
    if(eventNamesToBeConsumed().contains(notificationDTO.getEventName())){
      doAction(notificationDTO);
    }
    return str;
  }
}
