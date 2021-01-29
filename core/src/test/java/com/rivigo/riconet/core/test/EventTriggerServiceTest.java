package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.riconet.core.service.AppNotificationService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.DemurrageService;
import com.rivigo.riconet.core.service.EventTriggerService;
import com.rivigo.riconet.core.service.HolidayV2Service;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.riconet.core.service.RTOService;
import com.rivigo.riconet.core.service.TicketActionFactory;
import com.rivigo.riconet.core.service.TicketingClientService;
import com.rivigo.riconet.core.service.impl.DatastoreServiceImpl;
import com.rivigo.riconet.core.service.impl.EmailSenderServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.test.Utils.NotificationDTOModel;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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

  @Mock private ConsignmentService consignmentService;

  @Mock private PickupService pickupService;

  @Mock private AppNotificationService appNotificationService;

  @Mock private TicketActionFactory ticketActionFactory;

  @Mock private RTOService rtoService;

  @Mock private DemurrageService demurrageService;

  @Mock private HolidayV2Service holidayV2Service;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
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
    verify(consignmentService, times(1)).triggerBfCpdCalcualtion(any());
    verify(rtoService, times(1)).reassignRTOTicketIfExists(any());
  }

  @Test
  public void ticketActionEVentTest() {
    NotificationDTO notificationDTO =
        NotificationDTO.builder().eventName(EventName.TICKET_ACTION.name()).entityId(5l).build();
    eventTriggerService.processNotification(notificationDTO);
    verify(ticketActionFactory, times(1)).consume(eq(5l), any(), any(), any());
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

  @Test
  public void cnDeliveryEventTest() {
    Long entityId = 123456L;
    EventName eventName = EventName.CN_DELIVERY;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .entityId(entityId)
            .eventName(eventName.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(appNotificationService, times(1)).sendCnDeliveredNotification(notificationDTO);
    verify(demurrageService).processEventToEndDemurrage(notificationDTO);
  }

  @Test
  public void cnStaleEventTest() {
    Long entityId = 123456L;
    EventName eventName = EventName.CN_STALE;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId.toString());
    metadata.put("STALE_CATEGORY", "1");
    String staleEventName = eventName.name() + "_1";
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .entityId(entityId)
            .eventName(eventName.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(demurrageService).processEventToCancelDemurrage(notificationDTO);
    verify(ticketingClientService, times(1))
        .autoCloseTicket(entityId.toString(), TicketEntityType.CN.name(), staleEventName);
  }

  @Test
  public void cnDeletedEventTest() {
    Long entityId = 123456L;
    EventName eventName = EventName.CN_DELETED;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("OLD_CNOTE", entityId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .entityId(entityId)
            .eventName(eventName.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(demurrageService).processEventToCancelDemurrage(notificationDTO);
    verify(ticketingClientService, times(1))
        .autoCloseTicket(entityId.toString(), TicketEntityType.CN.name(), eventName.name());
  }

  @Test
  public void cnUndeliveryEventTest() {
    Long entityId = 123456L;
    EventName eventName = EventName.CN_UNDELIVERY;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .entityId(entityId)
            .eventName(eventName.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(demurrageService).processEventToStartDemurrage(notificationDTO);
  }

  @Test
  public void depsRecordCreationEventTest() {
    Long entityId = 123456L;
    EventName eventName = EventName.DEPS_RECORD_CREATION;
    Map<String, String> metadata = new HashMap<>();
    metadata.put("CNOTE", entityId.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .entityId(entityId)
            .eventName(eventName.name())
            .metadata(metadata)
            .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(demurrageService).processEventToCancelDemurrage(notificationDTO);
  }

  @Test
  public void holidayCreateEventTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.HOLIDAY_V2_CREATE);
    eventTriggerService.processNotification(notificationDTO);
    Mockito.verify(holidayV2Service, Mockito.times(1)).processHolidayEvent(notificationDTO, true);
  }

  @Test
  public void holidayUpdateEventTest() {
    NotificationDTO notificationDTO =
        NotificationDTOModel.getNotificationDTO(EventName.HOLIDAY_V2_UPDATE);
    eventTriggerService.processNotification(notificationDTO);
    Mockito.verify(holidayV2Service, Mockito.times(1)).processHolidayEvent(notificationDTO, false);
  }
}
