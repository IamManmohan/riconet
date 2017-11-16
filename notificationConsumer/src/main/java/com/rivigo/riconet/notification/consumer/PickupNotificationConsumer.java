package com.rivigo.riconet.notification.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class PickupNotificationConsumer extends ConsumerModel {

  @Autowired
  PickupService pickupService;

  ObjectMapper objectMapper ;

  public String processMessage(String str) throws IOException {
    List<PickupNotificationDTO> pickupNotificationDTOList=null;
    TypeReference<List<PickupNotificationDTO>> mapType = new TypeReference<List<PickupNotificationDTO>>() {};
    pickupNotificationDTOList= objectMapper.readValue(str, mapType);
    pickupService.processPickupNotificationDTOList(pickupNotificationDTOList);
    return str;
  }

  public PickupNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_PICKUP_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_PICKUP_NOTIFICATION_ERROR.name(),5l);
    objectMapper=new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
