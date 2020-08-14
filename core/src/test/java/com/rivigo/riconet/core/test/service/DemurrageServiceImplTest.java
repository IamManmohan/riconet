package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.ConsignmentReadOnlyServiceImpl;
import com.rivigo.riconet.core.service.impl.DemurrageServiceImpl;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.vas.Demurrage;
import com.rivigo.zoom.common.repository.mysql.vas.DemurrageRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
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
    long deliveryReattemptChargeable = 1L;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("UNDELIVERED_AT", startTime);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
    metadata.put("DELIVERY_REATTEMPT_CHARGEABLE", Long.toString(deliveryReattemptChargeable));
    metadata.put("ID", undeliveredId);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    ConsignmentReadOnly consignmentReadOnly = new ConsignmentReadOnly();
    consignmentReadOnly.setPromisedDeliveryDateTime(new DateTime(111111111));
    consignmentReadOnly.setCnoteType(CnoteType.NORMAL);
    consignmentReadOnly.setOrganizationId(1L);
    consignmentReadOnly.setIsActive(1);
    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(new Demurrage());
    Mockito.when(consignmentReadOnlyService.findConsignmentById(Mockito.anyLong()))
        .thenReturn(Optional.of(consignmentReadOnly));
    demurrageService.processEventToStartDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .startDemurrage(cnote, startTime, undeliveredId);
  }

  @Test
  public void processEventToStartDemurrageTest2() {
    Long consignmentId = 123456L;
    String cnote = "12345654321";
    String startTime = "123456789";
    String undeliveredId = "654321";
    long deliveryReattemptChargeable = 0L;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("UNDELIVERED_AT", startTime);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
    metadata.put("DELIVERY_REATTEMPT_CHARGEABLE", Long.toString(deliveryReattemptChargeable));
    metadata.put("ID", undeliveredId);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    ConsignmentReadOnly consignmentReadOnly = new ConsignmentReadOnly();
    consignmentReadOnly.setPromisedDeliveryDateTime(new DateTime(111111111));
    consignmentReadOnly.setCnoteType(CnoteType.NORMAL);
    consignmentReadOnly.setOrganizationId(1L);
    consignmentReadOnly.setIsActive(1);
    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(null);
    Mockito.when(consignmentReadOnlyService.findConsignmentById(Mockito.anyLong()))
        .thenReturn(Optional.of(consignmentReadOnly));
    // 1st Invocation returns from check and doesn't make backend API call.
    // Delivery reattempt chargeable is false and existing demurrage is null.
    demurrageService.processEventToStartDemurrage(notificationDTO);

    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(new Demurrage());
    consignmentReadOnly.setPromisedDeliveryDateTime(new DateTime(999999999));
    Mockito.when(consignmentReadOnlyService.findConsignmentById(Mockito.anyLong()))
        .thenReturn(Optional.of(consignmentReadOnly));
    // 2nd Invocation returns from check and doesn't make backend API call.
    // CPD is greater than undelivered_at time.
    demurrageService.processEventToStartDemurrage(notificationDTO);

    consignmentReadOnly.setPromisedDeliveryDateTime(new DateTime(111111111));
    consignmentReadOnly.setCnoteType(CnoteType.RETAIL);
    Mockito.when(consignmentReadOnlyService.findConsignmentById(Mockito.anyLong()))
        .thenReturn(Optional.of(consignmentReadOnly));
    // 3rd Invocation returns from check and doesn't make backend API call.
    // RETAIL type consignment.
    demurrageService.processEventToStartDemurrage(notificationDTO);

    // Verify that this function is never invoked in all 3 calls.
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(0))
        .startDemurrage(cnote, startTime, undeliveredId);
  }

  @Test
  public void processEventToEndDemurrageTest() {
    Long consignmentId = 123456L;
    ConsignmentStatus status = ConsignmentStatus.DELIVERED;
    String cnote = "12345654321";
    String deliveryTime = "123456789";
    CnoteType cnoteType = CnoteType.NORMAL;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("DELIVERY_DATE_TIME", deliveryTime);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
    metadata.put("STATUS", status.name());
    metadata.put("CNOTE_TYPE", cnoteType.name());
    metadata.put("ORGANIZATION_ID", "1");
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(new Demurrage());
    demurrageService.processEventToEndDemurrage(notificationDTO);
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).endDemurrage(cnote);
  }

  @Test
  public void processEventToEndDemurrageTest2() {
    Long consignmentId = 123456L;
    ConsignmentStatus status = ConsignmentStatus.DELIVERED;
    String cnote = "12345654321";
    String deliveryTime = "123456789";
    CnoteType cnoteType = CnoteType.NORMAL;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", cnote);
    metadata.put("CONSIGNMENT_ID", consignmentId.toString());
    metadata.put("STATUS", status.name());
    metadata.put("CNOTE_TYPE", cnoteType.name());
    metadata.put("ORGANIZATION_ID", "1");
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata).build();
    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(null);
    // 1st Invocation returns from check and doesn't make backend API call.
    // existingDemurrage is null.
    demurrageService.processEventToEndDemurrage(notificationDTO);

    metadata.put("DELIVERY_DATE_TIME", deliveryTime);
    notificationDTO.setMetadata(metadata);
    // 2nd Invocation returns from check and doesn't make backend API call.
    // deliveryDateTime is null.
    demurrageService.processEventToEndDemurrage(notificationDTO);

    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(new Demurrage());
    Map<String, String> metadata2 = new HashMap<>();
    metadata2.put("CNOTE", cnote);
    metadata2.put("CONSIGNMENT_ID", consignmentId.toString());
    metadata2.put("STATUS", ConsignmentStatus.UNDELIVERED.name());
    metadata2.put("CNOTE_TYPE", CnoteType.RETAIL.name());
    metadata2.put("ORGANIZATION_ID", "1");
    NotificationDTO notificationDTO2 =
        NotificationDTO.builder().entityId(consignmentId).metadata(metadata2).build();
    // 3rd Invocation returns from check and doesn't make backend API call.
    // Status is not DELIVERED or CnoteType RETAIL.
    demurrageService.processEventToEndDemurrage(notificationDTO2);

    // Verify that this function is never invoked in all 3 calls.
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(0)).endDemurrage(cnote);
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
    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(null);
    // 1st Invocation returns from check and doesn't make backend API call.
    // existingDemurrage is null.
    demurrageService.processEventToCancelDemurrage(notificationDTO);

    Mockito.when(demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(Mockito.anyLong()))
        .thenReturn(new Demurrage());
    // 2nd Invocation satisfies all the conditions and hence makes the backend API call.
    demurrageService.processEventToCancelDemurrage(notificationDTO);

    // Verify that this function is invoked only once out of total 2 calls made.
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).cancelDemurrage(cnote);
  }
}
