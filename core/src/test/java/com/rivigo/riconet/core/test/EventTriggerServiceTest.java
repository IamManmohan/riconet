package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketEntityType;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EventTriggerService;
import com.rivigo.riconet.core.service.HandoverService;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.riconet.core.service.QcService;
import com.rivigo.riconet.core.service.RTOService;
import com.rivigo.riconet.core.service.TicketingClientService;
import com.rivigo.riconet.core.service.impl.DatastoreServiceImpl;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class EventTriggerServiceTest {

  @InjectMocks private EventTriggerService eventTriggerService;

  @Mock private DatastoreServiceImpl datastoreService;

  @Mock private TicketingServiceImpl ticketingService;

  private EmailSenderServiceImpl emailSenderService;

  @Mock private RestTemplate restTemplate;

  @Mock private TicketingClientService ticketingClientService;

  @Mock private QcService qcService;

  @Mock private ConsignmentService consignmentService;

  @Mock private PickupService pickupService;

  @Mock private AppNotificationService appNotificationService;

  @Mock private HandoverService handoverService;

  @Mock private RTOService rtoService;

  @Captor private ArgumentCaptor<ConsignmentBasicDTO> consignmentBasicDTOArgumentCaptor;

  @Captor private ArgumentCaptor<ConsignmentCompletionEventDTO> consignmentCompletionEventDTOCaptor;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void cnStatusChangeFromReceivedAtOutest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("CONSIGNMENT_ID", "5");
    metadata.put("LOCATION_ID", "12");
    metadata.put("TO_LOCATION_ID", "13");
    metadata.put("STATUS", "LOADED");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1)).consumeLoadingEvent(consignmentBasicDTOArgumentCaptor.capture());
    Assert.assertEquals("1234567890", consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId() == 12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId() == 5l);
    Assert.assertEquals(
        ConsignmentStatus.LOADED, consignmentBasicDTOArgumentCaptor.getValue().getStatus());
  }

  @Test
  public void cnReceivedAtOutest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("CONSIGNMENT_ID", "5");
    metadata.put("LOCATION_ID", "12");
    metadata.put("TO_LOCATION_ID", "13");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_RECEIVED_AT_OU.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1)).consumeUnloadingEvent(consignmentBasicDTOArgumentCaptor.capture());
    verify(consignmentService, times(1)).triggerBfCpdCalcualtion(any());
    Assert.assertEquals("1234567890", consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId() == 12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId() == 5l);
  }

  @Test
  public void cnCompletionAllInstancestest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("CONSIGNMENT_ID", "5");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_COMPLETION_ALL_INSTANCES.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1))
        .consumeCompletionEvent(consignmentCompletionEventDTOCaptor.capture());
    Assert.assertEquals("1234567890", consignmentCompletionEventDTOCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentCompletionEventDTOCaptor.getValue().getConsignmentId() == 5l);
  }

  @Test
  public void cnCnoteTypeChangedFromNormaltest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("CONSIGNMENT_ID", "5");
    metadata.put("LOCATION_ID", "12");
    metadata.put("TO_LOCATION_ID", "13");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_CNOTE_TYPE_CHANGED_FROM_NORMAL.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1))
        .consumeCnoteTypeChangeEvent(consignmentBasicDTOArgumentCaptor.capture());
    Assert.assertEquals("1234567890", consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId() == 12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId() == 5l);
  }

  @Test
  public void cnDeliveryLoadedtest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("CONSIGNMENT_ID", "5");
    metadata.put("LOCATION_ID", "12");
    metadata.put("TO_LOCATION_ID", "13");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_DELIVERY_LOADED.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1))
        .consumeDeliveryLoadingEvent(consignmentBasicDTOArgumentCaptor.capture());
    Assert.assertEquals("1234567890", consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId() == 12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId() == 5l);
  }

  @Test
  public void cnCnoteChangeTest() {

    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    metadata.put("OLD_CNOTE", "9234567890");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_CNOTE_CHANGE.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1)).consumeCnoteChangeEvent("9234567890", "1234567890");
  }

  @Test
  public void cnDepsCreationTest() {

    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", "1234567890");
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_DEPS_CREATION.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1)).consumeDepsCreationEvent("1234567890", null);
  }

  @Test
  public void ticketActionEVentTest() {
    NotificationDTO notificationDTO =
        NotificationDTO.builder().eventName(EventName.TICKET_ACTION.name()).entityId(5l).build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService, times(1)).consumeQcBlockerTicketClosedEvent(eq(5l), any(), any());
    verify(handoverService, times(1)).consumeHandoverTicketAction(eq(5l), any(), any(), any());
  }

  @Test
  public void ticketingEventEmailTest() {

    NotificationDTO notificationDTO;
    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_CREATION);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_ASSIGNEE_CHANGE);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_STATUS_CHANGE);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_ESCALATION_CHANGE);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.TICKET_CC_NEW_PERSON_ADDITION);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_SEVERITY_CHANGE);
    eventTriggerService.processNotification(notificationDTO);

    notificationDTO = NotificationDTOModel.getNotificationDTO(EventName.TICKET_COMMENT_CREATION);
    eventTriggerService.processNotification(notificationDTO);
  }

  @Test
  public void ewaybillMetadataBasedCleanupTest() {

    NotificationDTO notificationDTO;
    notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CONSIGNMENT_EWAYBILL_METADATA_CREATION_ADDRESS_CLEANUP.name())
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(datastoreService, times(0))
        .cleanupAddressesUsingEwaybillMetadata(Matchers.eq(notificationDTO));
  }

  @Test
  public void cnTripDispatchedEventTest() {
    String entityId = "1234567890";
    EventName eventName = EventName.CN_TRIP_DISPATCHED;
    NotificationDTO notificationDTO;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId);
    notificationDTO =
        NotificationDTO.builder().eventName(eventName.name()).metadata(metadata).build();
    eventTriggerService.processNotification(notificationDTO);
    verify(appNotificationService, times(0))
        .sendCnFirstOuDispatchNotification(Matchers.eq(notificationDTO));
    verify(ticketingClientService, times(1))
        .autoCloseTicket(entityId, TicketEntityType.CN.name(), eventName.name());
  }

  @Test
  public void cnTripDispatchedEventTest1() {
    String entityId = "1234567890";
    EventName eventName = EventName.CN_TRIP_DISPATCHED;
    NotificationDTO notificationDTO;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId);
    metadata.put(ZoomCommunicationFieldNames.FIRST_RIVIGO_OU.name(), "TRUE");
    notificationDTO =
        NotificationDTO.builder().eventName(eventName.name()).metadata(metadata).build();
    eventTriggerService.processNotification(notificationDTO);
    verify(appNotificationService, times(1))
        .sendCnFirstOuDispatchNotification(Matchers.eq(notificationDTO));
    verify(ticketingClientService, times(1))
        .autoCloseTicket(entityId, TicketEntityType.CN.name(), eventName.name());
  }
}
