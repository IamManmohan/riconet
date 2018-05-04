package com.rivigo.riconet.core.test;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EventTriggerService;
import com.rivigo.riconet.core.service.QcService;
import com.rivigo.riconet.core.service.TicketingClientService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Ignore
@Slf4j
public class EventTriggerServiceTest {

  @InjectMocks
  private EventTriggerService eventTriggerService;

  @Mock
  private TicketingClientService ticketingClientService;

  @Mock
  private QcService qcService;

  @Mock
  private ConsignmentService consignmentService;

  @Captor
  private ArgumentCaptor<ConsignmentBasicDTO> consignmentBasicDTOArgumentCaptor;

  @Captor
  private ArgumentCaptor<ConsignmentCompletionEventDTO> consignmentCompletionEventDTOCaptor;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void cnStatusChangeFromReceivedAtOutest(){
    Map<String,String> metadata=new HashMap<>();
    metadata.put("CNOTE","1234567890");
    metadata.put("CONSIGNMENT_ID","5");
    metadata.put("LOCATION_ID","12");
    NotificationDTO notificationDTO=NotificationDTO.builder()
        .eventName(EventName.CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU)
        .metadata(metadata)
        .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService,times(1)).consumeLoadingEvent(consignmentBasicDTOArgumentCaptor.capture());
    Assert.assertEquals("1234567890",consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId()==12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId()==5l);
  }

  @Test
  public void cnReceivedAtOutest(){
    Map<String,String> metadata=new HashMap<>();
    metadata.put("CNOTE","1234567890");
    metadata.put("CONSIGNMENT_ID","5");
    metadata.put("LOCATION_ID","12");
    NotificationDTO notificationDTO=NotificationDTO.builder()
        .eventName(EventName.CN_RECEIVED_AT_OU)
        .metadata(metadata)
        .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService,times(1)).consumeUnloadingEvent(consignmentBasicDTOArgumentCaptor.capture());
    verify(consignmentService,times(1)).triggerBfCpdCalcualtion(any());
    Assert.assertEquals("1234567890",consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId()==12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId()==5l);
  }

  @Test
  public void cnCompletionAllInstancestest(){
    Map<String,String> metadata=new HashMap<>();
    metadata.put("CNOTE","1234567890");
    metadata.put("CONSIGNMENT_ID","5");
    NotificationDTO notificationDTO=NotificationDTO.builder()
        .eventName(EventName.CN_COMPLETION_ALL_INSTANCES)
        .metadata(metadata)
        .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService,times(1)).consumeCompletionEvent(consignmentCompletionEventDTOCaptor.capture());
    Assert.assertEquals("1234567890",consignmentCompletionEventDTOCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentCompletionEventDTOCaptor.getValue().getConsignmentId()==5l);
  }

  @Test
  public void cnCnoteTypeChangedFromNormaltest(){
    Map<String,String> metadata=new HashMap<>();
    metadata.put("CNOTE","1234567890");
    metadata.put("CONSIGNMENT_ID","5");
    metadata.put("LOCATION_ID","12");
    NotificationDTO notificationDTO=NotificationDTO.builder()
        .eventName(EventName.CN_CNOTE_TYPE_CHANGED_FROM_NORMAL)
        .metadata(metadata)
        .build();
    eventTriggerService.processNotification(notificationDTO);
    verify(qcService,times(1)).consumeCnoteTypeChangeEvent(consignmentBasicDTOArgumentCaptor.capture());
    Assert.assertEquals("1234567890",consignmentBasicDTOArgumentCaptor.getValue().getCnote());
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getLocationId()==12l);
    Assert.assertTrue(consignmentBasicDTOArgumentCaptor.getValue().getConsignmentId()==5l);
  }
}