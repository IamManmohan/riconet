package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.datastore.EwaybillMetadataDTO;
import com.rivigo.riconet.core.service.DatastoreService;
import com.rivigo.riconet.core.service.ZoomDatastoreAPIClientService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatastoreServiceImpl implements DatastoreService {

  private ObjectMapper objectMapper;

  private ZoomDatastoreAPIClientService zoomDatastoreAPIClientService;

  @Autowired
  public DatastoreServiceImpl(
      ObjectMapper objectMapper, ZoomDatastoreAPIClientService zoomDatastoreAPIClientService) {
    this.objectMapper = objectMapper;
    this.zoomDatastoreAPIClientService = zoomDatastoreAPIClientService;
  }

  @Override
  public void cleanupAddressesUsingEwaybillMetadata(NotificationDTO notificationDTO) {

    String eventName = notificationDTO.getEventName();
    log.info("Identified Event : {} ", eventName);
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (MapUtils.isEmpty(notificationDTO.getMetadata())) {
      log.info(
          "No metadata found for ewaybill metadata based cleanup of Event: {} EventUID: {} ",
          eventName,
          notificationDTO.getEventUID());
      return;
    }
    log.info("Event Metadata : {} ", metadata);

    EwaybillMetadataDTO ewaybillMetadataDTO =
        objectMapper.convertValue(metadata, EwaybillMetadataDTO.class);

    boolean cleanupSuccessful =
        zoomDatastoreAPIClientService.cleanupAddressesUsingEwaybillMetadata(ewaybillMetadataDTO);

    log.info(
        "ewaybill metadata based cleanup of Event: {} EventUID: {} : {}",
        eventName,
        notificationDTO.getEventUID(),
        cleanupSuccessful);
  }
}
