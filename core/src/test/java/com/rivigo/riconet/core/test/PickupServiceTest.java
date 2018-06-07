package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.ZoomBookAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.PickupServiceImpl;
import com.rivigo.zoom.common.dto.zoombook.ZoomBookTransactionRequestDTO;
import com.rivigo.zoom.common.enums.ConsignmentCompletionStatus;
import com.rivigo.zoom.common.enums.PickupStatus;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookFunctionType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTenantType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionHeader;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionSubHeader;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionType;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@Slf4j
public class PickupServiceTest {

  @InjectMocks private PickupServiceImpl pickupService;

  @Mock private ZoomBookAPIClientService zoomBookAPIClientService;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private PickupRepository pickupRepository;

  @Mock private ClientMasterService clientMasterService;

  @Mock private ConsignmentService consignmentService;

  @Spy private ObjectMapper objectMapper;

  @Captor private ArgumentCaptor<List<ZoomBookTransactionRequestDTO>> transactionListCaptor;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void deductPickupChargesPickupIdNullTest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.ORGANIZATION_ID.name(), "23");
    pickupService.deductPickupCharges(
        NotificationDTO.builder()
            .eventName(EventName.CN_COMPLETION_ALL_INSTANCES)
            .metadata(metadata)
            .build());
    verify(zoomBookAPIClientService, times(0)).processZoomBookTransaction(any());
  }

  @Test
  public void deductPickupChargesOrganizationIdNullTest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.PICK_UP_ID.name(), "23");
    pickupService.deductPickupCharges(
        NotificationDTO.builder()
            .eventName(EventName.CN_COMPLETION_ALL_INSTANCES)
            .metadata(metadata)
            .build());
    verify(zoomBookAPIClientService, times(0)).processZoomBookTransaction(any());
  }

  @Test
  public void deductPickupChargesOrganizationRivigoTest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.ORGANIZATION_ID.name(),
        String.valueOf(ConsignmentConstant.RIVIGO_ORGANIZATION_ID));
    metadata.put(ZoomCommunicationFieldNames.PICK_UP_ID.name(), "23");
    when(pickupRepository.findOne(any())).thenReturn(new Pickup());
    pickupService.deductPickupCharges(
        NotificationDTO.builder()
            .eventName(EventName.CN_COMPLETION_ALL_INSTANCES)
            .metadata(metadata)
            .build());
    verify(zoomBookAPIClientService, times(0)).processZoomBookTransaction(any());
  }

  @Test
  public void deductPickupChargesIncompleteConsignmentTest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.ORGANIZATION_ID.name(),
        String.valueOf(ConsignmentConstant.RIVIGO_ORGANIZATION_ID + 10));
    metadata.put(ZoomCommunicationFieldNames.PICK_UP_ID.name(), "23");
    ConsignmentReadOnly consignmentReadOnly1 = new ConsignmentReadOnly();
    consignmentReadOnly1.setId(10l);
    consignmentReadOnly1.setCompletionStatus(ConsignmentCompletionStatus.INCOMPLETE);
    ConsignmentReadOnly consignmentReadOnly2 = new ConsignmentReadOnly();
    consignmentReadOnly2.setId(11l);
    consignmentReadOnly2.setCompletionStatus(ConsignmentCompletionStatus.COMPLETE);
    Pickup pickup = new Pickup();
    pickup.setPickupStatus(PickupStatus.COMPLETE);
    pickup.setId(23l);
    when(pickupRepository.findOne(23l)).thenReturn(pickup);
    when(consignmentReadOnlyService.findConsignmentByPickupId(23l))
        .thenReturn(Arrays.asList(consignmentReadOnly1, consignmentReadOnly2));
    pickupService.deductPickupCharges(
        NotificationDTO.builder()
            .eventName(EventName.CN_COMPLETION_ALL_INSTANCES)
            .metadata(metadata)
            .build());
    verify(zoomBookAPIClientService, times(0)).processZoomBookTransaction(any());
  }

  @Test
  public void deductPickupChargesHappyCaseTest() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.ORGANIZATION_ID.name(), "100");
    metadata.put(ZoomCommunicationFieldNames.PICK_UP_ID.name(), "23");
    ConsignmentReadOnly consignmentReadOnly1 = new ConsignmentReadOnly();
    consignmentReadOnly1.setId(10l);
    consignmentReadOnly1.setWeight(10.0);
    consignmentReadOnly1.setCompletionStatus(ConsignmentCompletionStatus.COMPLETE);
    ConsignmentReadOnly consignmentReadOnly2 = new ConsignmentReadOnly();
    consignmentReadOnly2.setId(10l);
    consignmentReadOnly2.setWeight(20.0);
    consignmentReadOnly2.setCompletionStatus(ConsignmentCompletionStatus.COMPLETE);
    Pickup pickup = new Pickup();
    pickup.setPickupStatus(PickupStatus.COMPLETE);
    pickup.setId(23l);
    when(pickupRepository.findOne(23l)).thenReturn(pickup);
    when(consignmentReadOnlyService.findConsignmentByPickupId(23l))
        .thenReturn(Arrays.asList(consignmentReadOnly1, consignmentReadOnly2));
    when(consignmentService.isPrimaryConsignment(any())).thenReturn(true);
    when(zoomPropertyService.getDouble(ZoomPropertyName.MINIMUM_PICKUP_CHARGES_FOR_BF, 100.0))
        .thenReturn(100.0);
    when(zoomPropertyService.getDouble(ZoomPropertyName.BF_PICKUP_CHARGE_PER_KG, 1.3))
        .thenReturn(1.3);
    pickupService.deductPickupCharges(
        NotificationDTO.builder().eventName(EventName.CN_DELETED).metadata(metadata).build());
    verify(zoomBookAPIClientService, times(1))
        .processZoomBookTransaction(transactionListCaptor.capture());
    List<ZoomBookTransactionRequestDTO> transactionList = transactionListCaptor.getValue();
    Assert.assertTrue(transactionList.size() == 1);
    Assert.assertEquals(BigDecimal.valueOf(39.0), transactionList.get(0).getAmount());
    Assert.assertEquals("pickup|23|completion", transactionList.get(0).getClientRequestId());
    Assert.assertEquals(ZoomBookFunctionType.PASSBOOK, transactionList.get(0).getFunctionType());
    Assert.assertEquals(ZoomBookTenantType.BF, transactionList.get(0).getTenantType());
    Assert.assertTrue(100l == transactionList.get(0).getOrgId());
    Assert.assertEquals("pickup|23", transactionList.get(0).getReference());
    Assert.assertEquals(ZoomBookTransactionType.DEBIT, transactionList.get(0).getTransactionType());
    Assert.assertEquals(
        ZoomBookTransactionHeader.PICKUP, transactionList.get(0).getTransactionHeader());
    Assert.assertEquals(
        ZoomBookTransactionSubHeader.Creation, transactionList.get(0).getTransactionSubHeader());
  }

  @Test
  public void deductPickupChargesOnPickupCompletionHappyCaseTest() {
    Map<String, String> metadata = new HashMap<>();
    ConsignmentReadOnly consignmentReadOnly1 = new ConsignmentReadOnly();
    consignmentReadOnly1.setId(10l);
    consignmentReadOnly1.setWeight(10.0);
    consignmentReadOnly1.setCompletionStatus(ConsignmentCompletionStatus.COMPLETE);
    ConsignmentReadOnly consignmentReadOnly2 = new ConsignmentReadOnly();
    consignmentReadOnly2.setId(10l);
    consignmentReadOnly2.setWeight(20.0);
    consignmentReadOnly2.setCompletionStatus(ConsignmentCompletionStatus.COMPLETE);
    Pickup pickup = new Pickup();
    pickup.setPickupStatus(PickupStatus.COMPLETE);
    pickup.setId(23l);
    pickup.setClientCode("AMZN");

    Client client = new Client();
    client.setOrganizationId(100l);
    when(consignmentService.isPrimaryConsignment(any())).thenReturn(true);
    when(pickupRepository.findOne(23l)).thenReturn(pickup);
    when(clientMasterService.getClientByCode(pickup.getClientCode())).thenReturn(client);
    when(consignmentReadOnlyService.findConsignmentByPickupId(23l))
        .thenReturn(Arrays.asList(consignmentReadOnly1, consignmentReadOnly2));

    when(zoomPropertyService.getDouble(ZoomPropertyName.MINIMUM_PICKUP_CHARGES_FOR_BF, 100.0))
        .thenReturn(100.0);
    when(zoomPropertyService.getDouble(ZoomPropertyName.BF_PICKUP_CHARGE_PER_KG, 1.3))
        .thenReturn(1.3);
    pickupService.deductPickupCharges(
        NotificationDTO.builder()
            .entityId(23l)
            .eventName(EventName.PICKUP_COMPLETION)
            .metadata(metadata)
            .build());
    verify(zoomBookAPIClientService, times(1))
        .processZoomBookTransaction(transactionListCaptor.capture());
    List<ZoomBookTransactionRequestDTO> transactionList = transactionListCaptor.getValue();
    Assert.assertTrue(transactionList.size() == 1);
    Assert.assertEquals(BigDecimal.valueOf(39.0), transactionList.get(0).getAmount());
    Assert.assertEquals("pickup|23|completion", transactionList.get(0).getClientRequestId());
    Assert.assertEquals(ZoomBookFunctionType.PASSBOOK, transactionList.get(0).getFunctionType());
    Assert.assertEquals(ZoomBookTenantType.BF, transactionList.get(0).getTenantType());
    Assert.assertTrue(100l == transactionList.get(0).getOrgId());
    Assert.assertEquals("pickup|23", transactionList.get(0).getReference());
    Assert.assertEquals(ZoomBookTransactionType.DEBIT, transactionList.get(0).getTransactionType());
    Assert.assertEquals(
        ZoomBookTransactionHeader.PICKUP, transactionList.get(0).getTransactionHeader());
    Assert.assertEquals(
        ZoomBookTransactionSubHeader.Creation, transactionList.get(0).getTransactionSubHeader());
  }
}
