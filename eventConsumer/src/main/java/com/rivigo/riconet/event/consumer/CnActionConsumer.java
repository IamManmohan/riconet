package com.rivigo.riconet.event.consumer;

import com.google.common.base.Strings;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.HiltiApiService;
import com.rivigo.riconet.event.constants.ClientConstants;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CnActionConsumer extends EventConsumer {

  @Autowired private HiltiApiService hiltiApiService;

  @Override
  public List<EventName> eventNamesToBeConsumed() {
    return Arrays.asList(
        EventName.PICKUP_COMPLETION,
        EventName.CN_RECEIVED_AT_OU,
        EventName.CN_LOADED,
        EventName.CN_OUT_FOR_DELIVERY,
        EventName.CN_DELIVERY,
        EventName.CN_UNDELIVERY);
  }

  private void eventFactory(NotificationDTO notificationDTO) {

    String clientId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CLIENT_ID.name());

    if (Strings.isNullOrEmpty(clientId))
      throw new ZoomException("Client Id not found in the event {}", notificationDTO);
    switch (clientId) {
      case ClientConstants.HILTI_CLIENT_ID:
      case ClientConstants.HILTI_CLIENT_ID_DEP:
        hiltiApiService.addEventsToQueue(hiltiApiService.getRequestDtosByType(notificationDTO));
        break;
      default:
        log.info("No event defined for this client {}", notificationDTO);
        break;
    }
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    eventFactory(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return this.getClass().getName();
  }
}
