package com.rivigo.riconet.event.consumer;

import com.google.common.base.Strings;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ClientApiIntegrationService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CnActionConsumer extends EventConsumer {

  @Autowired private ClientApiIntegrationService clientApiIntegrationService;

  @Override
  public List<EventName> eventNamesToBeConsumed() {
    return Arrays.asList(
        EventName.PICKUP_COMPLETION,
        EventName.CN_RECEIVED_AT_OU,
        EventName.CN_LOADED,
        EventName.CN_DELIVERY_LOADED,
        EventName.CN_OUT_FOR_DELIVERY,
        EventName.CN_DELIVERY,
        EventName.CN_UNDELIVERY);
  }

  private void eventFactory(NotificationDTO notificationDTO) {

    String clientId =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CLIENT_ID.name());

    if (Strings.isNullOrEmpty(clientId))
      throw new ZoomException("Client Id not found in the event {}", notificationDTO);

    clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, clientId);
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    eventFactory(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return "CnActionConsumer";
  }
}
