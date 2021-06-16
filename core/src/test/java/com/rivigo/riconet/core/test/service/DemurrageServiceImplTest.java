package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.DemurrageServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DemurrageServiceImplTest {

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @InjectMocks private DemurrageServiceImpl demurrageService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processCnUndeliveryEventToStartDemurrageTest() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    String startTime = "123456789";
    String undeliveredId = "654321";
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), cnote);
    metadata.put(ZoomCommunicationFieldNames.Undelivery.UNDELIVERED_AT.name(), startTime);
    metadata.put(ZoomCommunicationFieldNames.ID.name(), undeliveredId);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    demurrageService.processCnUndeliveryEventToStartDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .startDemurrageOnCnUndelivery(cnote, startTime, undeliveredId);
  }

  @Test
  public void processCnDispatchDeliveryHoldEventToStartDemurrageTest() {
    String consignmentId = "123456";
    Long id = 123456L;
    String consignmentAlertId = "654321";
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name(), consignmentId);
    metadata.put(ZoomCommunicationFieldNames.CONSIGNMENT_ALERT_ID.name(), consignmentAlertId);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(id).metadata(metadata).build();
    demurrageService.processCnDispatchDeliveryHoldEventToStartDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .startDemurrageOnCnDispatchOrDeliveryHold(consignmentId, consignmentAlertId);
  }

  @Test
  public void processEventToEndDemurrageTest() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    String deliveryTime = "123456789";
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), cnote);
    metadata.put(ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name(), deliveryTime);
    metadata.put(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name(), consignmentId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    demurrageService.processEventToEndDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).endDemurrage(cnote);
  }

  @Test
  public void processEventToCancelDemurrageTest() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), cnote);
    metadata.put(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name(), consignmentId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    demurrageService.processEventToCancelDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).cancelDemurrage(cnote);
  }
}
