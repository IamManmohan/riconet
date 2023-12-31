package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PickupNotificationConsumer extends ConsumerModel {

  @Autowired private PickupService pickupService;

  private ObjectMapper objectMapper;

  public PickupNotificationConsumer() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_PICKUP_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_PICKUP_NOTIFICATION_ERROR.name();
  }

  public void processMessage(String str) throws IOException {
    List<PickupNotificationDTO> pickupNotificationDTOList = null;
    TypeReference<List<PickupNotificationDTO>> mapType =
        new TypeReference<List<PickupNotificationDTO>>() {};
    pickupNotificationDTOList = objectMapper.readValue(str, mapType);
    pickupService.processPickupNotificationDTOList(pickupNotificationDTOList);
  }
}
