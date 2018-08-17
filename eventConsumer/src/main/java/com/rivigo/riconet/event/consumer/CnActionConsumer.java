package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.HiltiApiService;
import java.util.Arrays;
import java.util.List;

import com.rivigo.riconet.event.constants.ClientConstants;
import com.rivigo.zoom.exceptions.ZoomException;
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
        EventName.CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU,
        EventName.CN_OUT_FOR_DELIVERY,
        EventName.CN_DELIVERY,
        EventName.CN_UNDELIVERY);
  }

  private void eventFactory(NotificationDTO notificationDTO) {

    String clientId;
    try {
      clientId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CLIENT_ID);
    } catch (NumberFormatException ne) {
      throw new ZoomException("Unable to get clientId from notificationDTO {}", notificationDTO, ne);
    }

    switch (clientId) {
      case ClientConstants.HILTI_CLIENT_ID:
        hiltiApiService.getRequestDtosByType(notificationDTO).forEach(v-> hiltiApiService.addEventToQueue(v));
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
