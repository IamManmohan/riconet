package com.rivigo.riconet.core.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.constants.EmailConstant;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.ZoomCommunicationsEventDTO;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.ConsignmentCodDodService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomBillingAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.QcServiceImpl;
import com.rivigo.riconet.core.service.impl.TicketingServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomTicketingAPIClientServiceImpl;
import com.rivigo.riconet.ruleengine.QCRuleEngine;
import com.rivigo.zoom.common.dto.client.ClientClusterMetadataDTO;
import com.rivigo.zoom.common.dto.client.ClientPincodeMetadataDTO;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.enums.ruleengine.RuleType;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentCodDod;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.model.ruleengine.RuleEngineRule;
import com.rivigo.zoom.common.repository.mysql.ruleengine.RuleEngineRuleRepository;
import com.rivigo.zoom.common.repository.redis.QcBlockerActionParamsRedisRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@Slf4j
public class QcServiceTest {

  @InjectMocks private QcServiceImpl qcService;

  private ZoomTicketingAPIClientServiceImpl zoomTicketingAPIClientService =
      Mockito.mock(ZoomTicketingAPIClientServiceImpl.class);

  @Spy private ObjectMapper objectMapper;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private ConsignmentService consignmentService;

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock private LocationService locationService;

  @Spy private QCRuleEngine qcRuleEngine;

  @Mock private RuleEngineRuleRepository ruleEngineRuleRepository;

  @Mock private ConsignmentCodDodService consignmentCodDodService;

  @Mock private SmsService smsService;

  @Mock private ZoomBillingAPIClientService zoomBillingAPIClientService;

  @Mock private UserMasterService userMasterService;

  @Mock private EmailService emailService;

  @Mock private AdministrativeEntityService administrativeEntityService;

  @Mock private QcBlockerActionParamsRedisRepository qcBlockerActionParamsRedisRepository;

  @Mock private ConsignmentScheduleService consignmentScheduleService;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private TicketingServiceImpl ticketingService =
      new TicketingServiceImpl(null, null, null, zoomTicketingAPIClientService);

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    org.springframework.test.util.ReflectionTestUtils.setField(
        qcRuleEngine, "ruleEngineRuleRepository", ruleEngineRuleRepository);
    Mockito.when(zoomBillingAPIClientService.getChargedWeightForConsignment(anyString()))
        .thenReturn(10.0);
    org.springframework.test.util.ReflectionTestUtils.setField(
        qcService, "ticketingService", ticketingService);
  }

  @Test
  public void consumeLoadingEventNoTicketTest1() {
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Collections.emptyList());
    qcService.consumeLoadingEvent(data);
  }

  @Test
  public void consumeLoadingEventAtRivigoLocationTest2() {
    ZoomCommunicationsEventDTO eventDTO = new ZoomCommunicationsEventDTO();
    eventDTO.setEventName(EventName.CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU.name());
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    eventDTO.setMetadata(objectMapper.convertValue(data, Map.class));
    // already closed ticket no edits happen
    TicketDTO ticket1 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.CLOSED)
            .id(1l)
            .build();
    // inprogress measurement Qc ticket gets closed
    TicketDTO ticket2 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(2l)
            .build();

    // new measurement Qc ticket gets edited to in_progress and then gets closed
    TicketDTO ticket3 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.NEW)
            .id(2l)
            .build();

    // already closed ticket no edits happen
    TicketDTO ticket4 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.CLOSED)
            .id(3l)
            .build();

    // open re-check qc progress to next location
    TicketDTO ticket5 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(4l)
            .build();
    List<ConsignmentSchedule> consignmentSchedules = new ArrayList<>();
    ConsignmentSchedule consignmentSchedule = new ConsignmentSchedule();
    consignmentSchedule.setLocationType(LocationTypeV2.LOCATION);
    consignmentSchedule.setSequence(2);
    consignmentSchedule.setLocationId(1L);
    consignmentSchedules.add(consignmentSchedule);

    when(consignmentScheduleService.getActivePlan(Mockito.anyLong()))
        .thenReturn(consignmentSchedules);
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Arrays.asList(ticket1, ticket2, ticket3, ticket4, ticket5));
    Location location = new Location();
    location.setId(2L);
    location.setOrganizationId(1l);
    when(locationService.getLocationById(any())).thenReturn(location);
    qcService.consumeLoadingEvent(data);
    verify(zoomTicketingAPIClientService, times(0)).editTicket(ticket1);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket2);
    Assert.assertEquals(TicketStatus.IN_PROGRESS, ticket2.getStatus());
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket3);
    Assert.assertEquals(TicketStatus.NEW, ticket3.getStatus());
    verify(zoomTicketingAPIClientService, times(0)).editTicket(ticket4);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket5);
    Assert.assertEquals(TicketStatus.IN_PROGRESS, ticket5.getStatus());
    Assert.assertEquals(AssigneeType.NONE, ticket5.getAssigneeType());
  }

  @Test
  public void consumeLoadingEventAtBfLocationTest2() {
    ZoomCommunicationsEventDTO eventDTO = new ZoomCommunicationsEventDTO();
    eventDTO.setEventName(EventName.CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU.name());
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    eventDTO.setMetadata(objectMapper.convertValue(data, Map.class));
    // open measurement Qc ticket gets unassigned
    TicketDTO ticket1 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(2l)
            .build();

    // open measurement Qc ticket gets unassigned
    TicketDTO ticket2 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(4l)
            .build();

    List<ConsignmentSchedule> consignmentSchedules = new ArrayList<>();
    ConsignmentSchedule consignmentSchedule = new ConsignmentSchedule();
    consignmentSchedule.setLocationType(LocationTypeV2.LOCATION);
    consignmentSchedule.setSequence(2);
    consignmentSchedule.setLocationId(1L);
    consignmentSchedules.add(consignmentSchedule);

    when(consignmentScheduleService.getActivePlan(Mockito.anyLong()))
        .thenReturn(consignmentSchedules);
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Arrays.asList(ticket1, ticket2));
    Location location = new Location();
    location.setId(2L);
    location.setOrganizationId(1000l);
    when(locationService.getLocationById(any())).thenReturn(location);
    qcService.consumeLoadingEvent(data);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket1);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket2);
    Assert.assertEquals(TicketStatus.IN_PROGRESS, ticket1.getStatus());
    Assert.assertEquals(AssigneeType.NONE, ticket1.getAssigneeType());
    Assert.assertEquals(TicketStatus.IN_PROGRESS, ticket2.getStatus());
    Assert.assertEquals(AssigneeType.NONE, ticket2.getAssigneeType());
  }

  @Test
  public void consumeUnloadingEventNoTicketTest1() {
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Collections.emptyList());
    qcService.consumeUnloadingEvent(data);
  }

  @Test
  public void consumeUnloadingEventGroupExistTest() {
    ZoomCommunicationsEventDTO eventDTO = new ZoomCommunicationsEventDTO();
    eventDTO.setEventName(EventName.CN_RECEIVED_AT_OU.name());
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    eventDTO.setMetadata(objectMapper.convertValue(data, Map.class));

    AdministrativeEntity aem = new AdministrativeEntity();
    aem.setCode("DEL");

    // closed ticket is not edited
    TicketDTO ticket1 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.CLOSED)
            .id(1l)
            .build();
    // measurment task is assigned to location group
    TicketDTO ticket2 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(2l)
            .build();
    // closed ticket is not edited
    TicketDTO ticket3 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.CLOSED)
            .id(3l)
            .build();
    // re-check task is assigned to location group
    TicketDTO ticket4 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(4l)
            .build();
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Arrays.asList(ticket1, ticket2, ticket3, ticket4));
    when(zoomTicketingAPIClientService.getGroupId(
            data.getLocationId(), ZoomTicketingConstant.QC_GROUP_NAME, LocationType.OU))
        .thenReturn(GroupDTO.builder().id(39l).build());
    when(administrativeEntityService.findParentCluster(any())).thenReturn(aem);

    qcService.consumeUnloadingEvent(data);
    verify(zoomTicketingAPIClientService, times(0)).editTicket(ticket1);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket2);
    Assert.assertTrue(ticket2.getAssigneeId().equals(39l));
    Assert.assertEquals(AssigneeType.GROUP, ticket2.getAssigneeType());
    verify(zoomTicketingAPIClientService, times(0)).editTicket(ticket3);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticket4);
    Assert.assertTrue(ticket4.getAssigneeId().equals(39l));
    Assert.assertEquals(AssigneeType.GROUP, ticket4.getAssigneeType());
  }

  @Test
  public void consumeUnloadingEventGroupDoesnotExistAndRivigoLocationTest() {
    ZoomCommunicationsEventDTO eventDTO = new ZoomCommunicationsEventDTO();
    eventDTO.setEventName(EventName.CN_RECEIVED_AT_OU.name());
    ConsignmentBasicDTO data = new ConsignmentBasicDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    data.setLocationId(15l);
    eventDTO.setMetadata(objectMapper.convertValue(data, Map.class));

    AdministrativeEntity aem = new AdministrativeEntity();
    aem.setCode("DEL");

    // measurment task will be closed
    TicketDTO ticket1 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(2l)
            .build();
    // measurment task will be closed
    TicketDTO ticket2 =
        TicketDTO.builder()
            .typeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
            .status(TicketStatus.IN_PROGRESS)
            .id(4l)
            .build();
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq(data.getCnote()), any()))
        .thenReturn(Arrays.asList(ticket1, ticket2));
    Location location = new Location();
    location.setOrganizationId(1l);
    when(locationService.getLocationById(any())).thenReturn(location);
    when(administrativeEntityService.findParentCluster(any())).thenReturn(aem);

    qcService.consumeUnloadingEvent(data);
    verify(zoomTicketingAPIClientService, times(2)).editTicket(ticket1);
    verify(zoomTicketingAPIClientService, times(2)).editTicket(ticket2);
    verify(zoomBackendAPIClientService, times(1)).updateQcCheck(any(), any());
    Assert.assertEquals(TicketStatus.CLOSED, ticket1.getStatus());
    Assert.assertEquals(TicketStatus.CLOSED, ticket2.getStatus());
  }

  @Test
  public void consumeCnCreationEventTestInvalidId() {
    ConsignmentCompletionEventDTO data = new ConsignmentCompletionEventDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("No consignment exists with id :1");
    qcService.consumeCompletionEvent(data);
  }

  @Test
  public void consumeCnCreationEventTest2() {
    ConsignmentCompletionEventDTO data = new ConsignmentCompletionEventDTO();
    data.setConsignmentId(1l);
    data.setCnote("1234567890");
    Consignment consignment = new Consignment();
    ConsignmentCodDod consignmentCodDod = new ConsignmentCodDod();
    consignmentCodDod.setPaymentType(PaymentType.CHEQUE);
    when(consignmentService.getConsignmentById(1l)).thenReturn(consignment);
    // Following exception shouldn't stop the flow
    doThrow(new ZoomException()).when(zoomBackendAPIClientService).triggerPolicyGeneration(1l);
    when(consignmentCodDodService.getActiveCodDod(consignment.getId()))
        .thenReturn(consignmentCodDod);
    qcService.consumeCompletionEvent(data);
    verify(smsService, times(1)).sendSms(any(), any());
    verify(zoomBackendAPIClientService, times(1)).triggerPolicyGeneration(1l);
  }

  //  @Test
  //  //positive test case when task creation is not required
  //  public void checkTest1(){
  //
  //    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
  //    Consignment consignment = getConsignmentDTO();
  //    mockingParamsForCheckFunction();
  //    boolean result = qcService.check(consignmentCompletionEventDTO,consignment);
  //    assertEquals(result,false);
  //
  //  }
  //
  //  @Test
  //  //test case for different business logic
  //  public void checkTest2(){
  //
  //    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
  //    Consignment consignment = getConsignmentDTO();
  //    mockingParamsForCheckFunction();
  //    boolean result = qcService.check(consignmentCompletionEventDTO,consignment);
  //    assertEquals(result,false);
  //
  //  }

  @Test
  // negative test case when condition fails for min_weight
  public void checkTest3() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMinWeight(11.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  // negative test case when condition fails for max_weight
  public void checkTest4() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMaxWeight(10.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  // negative test case when condition fails for min__charged_weight
  public void checkTest5() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMinChargedWeightPerWeight(11.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  // negative test case when condition fails for max__charged_weight
  public void checkTest6() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMaxChargedWeightPerWeight(10.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  // negative test case when condition fails for min_invoice_per_weight
  public void checkTest7() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMinInvoicePerWeight(110.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  // negative test case when condition fails for max_invoice_per_weight
  public void checkTest8() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment = getConsignmentDTO();
    consignmentCompletionEventDTO.getClientPincodeMetadataDTO().setMaxInvoicePerWeight(90.0);
    mockingParamsForCheckFunction();
    boolean result = qcService.check(consignmentCompletionEventDTO, consignment);
    assertEquals(result, true);
  }

  @Test
  public void isMeasurementQcRequiredTest() {
    ConsignmentCompletionEventDTO completionEventDTO = new ConsignmentCompletionEventDTO();
    Assert.assertFalse(qcService.isMeasurementQcRequired(completionEventDTO));

    completionEventDTO.setClientClusterMetadataDTO(new ClientClusterMetadataDTO());
    completionEventDTO.getClientClusterMetadataDTO().setMeasurementCheckNeeded(Boolean.FALSE);
    Assert.assertFalse(qcService.isMeasurementQcRequired(completionEventDTO));

    completionEventDTO.getClientClusterMetadataDTO().setMeasurementCheckNeeded(Boolean.TRUE);
    Assert.assertFalse(qcService.isMeasurementQcRequired(completionEventDTO));

    completionEventDTO.getClientClusterMetadataDTO().setQcMeasurementTicketProbability(0.0);
    Assert.assertFalse(qcService.isMeasurementQcRequired(completionEventDTO));

    completionEventDTO.getClientClusterMetadataDTO().setQcMeasurementTicketProbability(100.0);
    Assert.assertTrue(qcService.isMeasurementQcRequired(completionEventDTO));
  }

  @Test
  public void consumeCnoteChangeEventTest() {
    TicketDTO ticketDTO = new TicketDTO();
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(eq("1234567890"), any()))
        .thenReturn(Collections.singletonList(ticketDTO));
    qcService.consumeCnoteChangeEvent("1234567890", "9234567890");
    verify(zoomTicketingAPIClientService, times(1)).editTicket(any());
    Assert.assertEquals(ticketDTO.getEntityId(), "9234567890");
  }

  @Test
  public void qcActionNonBlockerTest() {
    when(zoomTicketingAPIClientService.getTicketByTicketId(5l))
        .thenReturn(
            TicketDTO.builder()
                .typeId(ZoomTicketingConstant.QC_BLOCKER_TYPE_ID + 1)
                .status(TicketStatus.CLOSED)
                .build());
    qcService.consumeQcBlockerTicketClosedEvent(5l, 10l);
    verify(zoomBackendAPIClientService, times(0)).handleQcBlockerClosure(5l);
  }

  @Test
  public void qcBlockerActionTest() {
    when(zoomTicketingAPIClientService.getTicketByTicketId(5l))
        .thenReturn(
            TicketDTO.builder()
                .typeId(ZoomTicketingConstant.QC_BLOCKER_TYPE_ID)
                .status(TicketStatus.CLOSED)
                .build());
    qcService.consumeQcBlockerTicketClosedEvent(5l, 10l);
    verify(zoomBackendAPIClientService, times(1)).handleQcBlockerClosure(5l);
  }

  @Test
  public void consumeDepsCreationEventChildCnoteTest() {
    TicketDTO ticketDTO1 = TicketDTO.builder().status(TicketStatus.IN_PROGRESS).build();
    TicketDTO ticketDTO2 = TicketDTO.builder().status(TicketStatus.CLOSED).build();
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            "1234567890", qcService.getQcTicketTypes()))
        .thenReturn(Arrays.asList(ticketDTO1, ticketDTO2));
    Consignment consignment = new Consignment();
    consignment.setCnote("1234567890");
    when(consignmentService.getConsignmentByCnote("1234567890")).thenReturn(consignment);
    qcService.consumeDepsCreationEvent("1234567890-1", null);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticketDTO1);
    Assert.assertEquals(ticketDTO1.getStatus(), TicketStatus.CLOSED);
  }

  @Test
  public void consumeDepsCreationEventTest() {
    TicketDTO ticketDTO1 = TicketDTO.builder().status(TicketStatus.IN_PROGRESS).build();
    TicketDTO ticketDTO2 = TicketDTO.builder().status(TicketStatus.CLOSED).build();
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            "1234567890", qcService.getQcTicketTypes()))
        .thenReturn(Arrays.asList(ticketDTO1, ticketDTO2));
    Consignment consignment = new Consignment();
    consignment.setCnote("1234567890");
    when(consignmentService.getConsignmentByCnote("1234567890")).thenReturn(consignment);
    qcService.consumeDepsCreationEvent("1234567890-1", null);
    verify(zoomTicketingAPIClientService, times(1)).editTicket(ticketDTO1);
    Assert.assertEquals(ticketDTO1.getStatus(), TicketStatus.CLOSED);
  }

  @Test
  public void consumeQcBlockerTicketClosedEventNullTest() {
    qcService.consumeQcBlockerTicketClosedEvent(null, 10l);
    verify(zoomBackendAPIClientService, times(0)).handleQcBlockerClosure(any());
  }

  private void mockingParamsForCheckFunction() {
    Mockito.when(ruleEngineRuleRepository.findByRuleTypeAndIsActive(RuleType.BASIC_RULE, true))
        .thenReturn(getBasicRuleList());
    Mockito.when(
            ruleEngineRuleRepository.findByRuleNameAndRuleTypeAndIsActive(
                "QC_CHECK", RuleType.BUSINESS_RULE, true))
        .thenReturn(getBusinessRuleList2());
    Mockito.when(
            zoomPropertyService.getDouble(ZoomPropertyName.MINIMUM_NUMBER_OF_CN_REQUIRED, 30.0))
        .thenReturn(30.0);
    Mockito.when(
            zoomPropertyService.getString(
                ZoomPropertyName.REQUIRED_CLIENT_TYPE, CnoteType.NORMAL.name()))
        .thenReturn(CnoteType.NORMAL.name());
  }

  @Test
  public void consumeQcBlockerTicketCreationEventTest() {
    when(zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            "1234567890",
            Collections.singletonList(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString())))
        .thenReturn(Collections.singletonList(new TicketDTO()));
    Client client = new Client();
    client.setClientNotificationList(Collections.singleton("dummy@rivigo.com"));

    Consignment consignment = new Consignment();
    consignment.setId(100l);
    consignment.setClient(client);
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
    when(consignmentService.getConsignmentByCnote(any())).thenReturn(consignment);
    when(locationService.getLocationById(any())).thenReturn(new Location());
    when(userMasterService.getById(any())).thenReturn(new User());
    qcService.consumeQcBlockerTicketCreationEvent(
        5l, "1234567890", ZoomTicketingConstant.QC_BLOCKER_TYPE_ID);
    verify(emailService, times(1))
        .sendEmail(eq(EmailConstant.SERVICE_EMAIL_ID), any(), any(), any(), any(), any(), any());
  }

  private List<RuleEngineRule> getBasicRuleList() {

    String rule1 = "ACTUAL_WEIGHT MAX_WEIGHT < ";
    String rule2 = "ACTUAL_WEIGHT MIN_WEIGHT > ";

    String rule3 = "CHARGED_WEIGHT_PER_WEIGHT MAX_CHARGED_WEIGHT_PER_WEIGHT < ";
    String rule4 = "CHARGED_WEIGHT_PER_WEIGHT MIN_CHARGED_WEIGHT_PER_WEIGHT > ";

    String rule5 = "INVOICE_VALUE_PER_WEIGHT MAX_INVOICE_VALUE_PER_WEIGHT < ";
    String rule6 = "INVOICE_VALUE_PER_WEIGHT MIN_INVOICE_VALUE_PER_WEIGHT > ";

    String rule7 = "NUMBER_OF_CN MINIMUM_NUMBER_OF_CN_REQUIRED >";
    String rule8 = "CLIENT_TYPE REQUIRED_CLIENT_TYPE =";

    List<String> basicRuleStringList = new ArrayList<>();
    basicRuleStringList.add(rule1);
    basicRuleStringList.add(rule2);
    basicRuleStringList.add(rule3);
    basicRuleStringList.add(rule4);
    basicRuleStringList.add(rule5);
    basicRuleStringList.add(rule6);
    basicRuleStringList.add(rule7);
    basicRuleStringList.add(rule8);

    List<RuleEngineRule> basicRuleList = new ArrayList<>();
    for (int i = 0; i < basicRuleStringList.size(); ++i) {
      RuleEngineRule basicRule = new RuleEngineRule();
      basicRule.setId(i);
      basicRule.setRule(basicRuleStringList.get(i));
      basicRule.setRuleName("rule" + i);
      basicRuleList.add(basicRule);
    }

    return basicRuleList;
  }

  private List<RuleEngineRule> getBusinessRuleList1() {

    // #7 AND #8 AND ( #1 AND #2 OR #3 AND #4 OR #5 AND #6)
    // #7 #8 AND #1 #2 AND #3 #4 AND OR #5 #6 AND OR AND
    String businessRule1 = "#6 #7 AND #0 #1 OR #2 #3 AND AND #4 #5 AND AND NOT AND";
    RuleEngineRule businessRule = new RuleEngineRule();
    businessRule.setId(1);
    businessRule.setRuleName("QC_CHECK");
    businessRule.setActive(true);
    businessRule.setPriority(1);
    businessRule.setRule(businessRule1);

    List<RuleEngineRule> businessRuleList = new ArrayList<>();
    businessRuleList.add(businessRule);
    return businessRuleList;
  }

  private List<RuleEngineRule> getBusinessRuleList2() {

    // #7 AND #8 AND ( #1 AND #2 OR #3 AND #4 OR #5 AND #6)
    // #7 #8 AND #1 #2 AND #3 #4 AND OR #5 #6 AND OR AND
    String businessRule1 = "#6 #7 AND #0 #1 AND #2 #3 AND AND #4 #5 AND AND NOT AND";
    RuleEngineRule businessRule = new RuleEngineRule();
    businessRule.setId(1);
    businessRule.setRuleName("QC_CHECK");
    businessRule.setActive(true);
    businessRule.setPriority(1);
    businessRule.setRule(businessRule1);

    List<RuleEngineRule> businessRuleList = new ArrayList<>();
    businessRuleList.add(businessRule);
    return businessRuleList;
  }

  private ConsignmentCompletionEventDTO getConsignmentCompletionDTO() {

    ConsignmentCompletionEventDTO consignmentCompletionEventDTO =
        new ConsignmentCompletionEventDTO();
    ClientPincodeMetadataDTO clientPincodeMetadataDTO = new ClientPincodeMetadataDTO();
    ClientClusterMetadataDTO clientClusterMetadataDTO = new ClientClusterMetadataDTO();

    clientPincodeMetadataDTO.setCount((long) 31);
    clientPincodeMetadataDTO.setMinWeight(8.0);
    clientPincodeMetadataDTO.setMaxWeight(12.0);
    clientPincodeMetadataDTO.setMinChargedWeightPerWeight(8.0);
    clientPincodeMetadataDTO.setMaxChargedWeightPerWeight(12.0);
    clientPincodeMetadataDTO.setMinInvoicePerWeight(80.0);
    clientPincodeMetadataDTO.setMaxInvoicePerWeight(120.0);

    consignmentCompletionEventDTO.setClientClusterMetadataDTO(clientClusterMetadataDTO);
    consignmentCompletionEventDTO.setClientPincodeMetadataDTO(clientPincodeMetadataDTO);
    consignmentCompletionEventDTO.setCnote("NORMAL");

    return consignmentCompletionEventDTO;
  }

  private Consignment getConsignmentDTO() {
    Consignment consignment = new Consignment();
    consignment.setWeight(10.0);
    consignment.setValue(100.0);
    consignment.setCnoteType(CnoteType.NORMAL);
    // consignment.setOrganizationId(1L);

    return consignment;
  }
}
