package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.ConsignmentReadOnlyServiceImpl;
import com.rivigo.riconet.core.service.impl.DemurrageServiceImpl;
import com.rivigo.zoom.common.repository.mysql.vas.DemurrageRepository;
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

  @Mock private ConsignmentReadOnlyServiceImpl consignmentReadOnlyService;

  @Mock private DemurrageRepository demurrageRepository;

  @InjectMocks private DemurrageServiceImpl demurrageService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processEventToStartDemurrageTest() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    String startTime = "123456789";
    String undeliveredId = "654321";
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("UNDELIVERED_AT", startTime);
    metadata.put("ID", undeliveredId);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    demurrageService.processCnUndeliveryEventToStartDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .startDemurrageOnCnUndelivery(cnote, startTime, undeliveredId);
  }

  @Test
  public void processEventToEndDemurrageTest() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    String deliveryTime = "123456789";
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("DELIVERY_DATE_TIME", deliveryTime);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
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
    metadata.put("CNOTE", cnote);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    demurrageService.processEventToCancelDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).cancelDemurrage(cnote);
  }
}
