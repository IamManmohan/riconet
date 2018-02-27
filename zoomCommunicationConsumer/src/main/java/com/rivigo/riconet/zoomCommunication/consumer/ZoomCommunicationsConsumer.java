package com.rivigo.riconet.zoomCommunication.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.ZoomCommunicationsService;
import com.rivigo.zoom.common.enums.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by aditya on 22/2/18.
 */
@Slf4j
@Component
public class ZoomCommunicationsConsumer extends ConsumerModel {

  private ObjectMapper objectMapper ;

  @Autowired
  private ZoomCommunicationsService zoomCommunicationsService;

  @Override
  public String processMessage(String str) throws IOException {
//    List<PickupNotificationDTO> pickupNotificationDTOList=null;
//    TypeReference<List<PickupNotificationDTO>> mapType = new TypeReference<List<PickupNotificationDTO>>() {};
//    pickupNotificationDTOList= objectMapper.readValue(str, mapType);
    zoomCommunicationsService.processNotificationMessage(str);
    return str;
  }

  public ZoomCommunicationsConsumer() {
    super(Topic.COM_RIVIGO_ZOOM_COMMUNICATIONS_CN_CREATION.name(),Topic.COM_RIVIGO_ZOOM_COMMUNICATIONS_CN_CREATION_ERROR.name(),5l);
    objectMapper=new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

}
