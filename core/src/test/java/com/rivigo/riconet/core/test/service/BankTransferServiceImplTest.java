package com.rivigo.riconet.core.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.dto.UniqueTransactionReferencePostingDTO;
import com.rivigo.finance.zoom.enums.UniqueTransactionReferencePostingStatus;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.UploadedFileRecordService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.service.impl.BankTransferServiceImpl;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BankTransferServiceImplTest {

  @Mock private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Mock private UploadedFileRecordService uploadedFileRecordService;

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock private ObjectMapper objectMapper;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks private BankTransferServiceImpl bankTransferService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createTicketWhenUploadedFilePresentForCn() {
    TicketDTO ticketDTO = TicketDTO.builder().id(1L).build();
    Mockito.when(zoomTicketingAPIClientService.createTicket(Mockito.any())).thenReturn(ticketDTO);
    Mockito.when(consignmentReadOnlyService.findRequiredById(Mockito.anyLong()))
        .thenReturn(new ConsignmentReadOnly());
    Mockito.when(
            uploadedFileRecordService.getByEntityAndFileType(
                Mockito.eq(EntityType.CONSIGNMENT),
                Mockito.anyString(),
                Mockito.eq(FileTypes.BANK_TRANSFER)))
        .thenReturn(Collections.singletonList(new UploadedFileRecord()));
    Mockito.when(
            zoomTicketingAPIClientService.getByEntityInAndType(
                Mockito.anyList(), Mockito.anyString()))
        .thenReturn(Collections.emptyList());
    expectedException.expect(ZoomException.class);
    bankTransferService.createTicket(getMockMetadataInput());
    Mockito.when(
            zoomTicketingAPIClientService.getByEntityInAndType(
                Mockito.anyList(), Mockito.anyString()))
        .thenReturn(Collections.singletonList(ticketDTO));
    expectedException.expect(ZoomException.class);
    bankTransferService.createTicket(getMockMetadataInput());
    Mockito.verify(zoomTicketingAPIClientService, Mockito.times(1)).createTicket(Mockito.any());
  }

  @Test
  public void createTicketWhenUploadedFilePresentForPickup() {
    TicketDTO ticketDTO = TicketDTO.builder().id(1L).build();
    Mockito.when(zoomTicketingAPIClientService.createTicket(Mockito.any())).thenReturn(ticketDTO);
    Mockito.when(consignmentReadOnlyService.findRequiredById(Mockito.anyLong()))
        .thenReturn(new ConsignmentReadOnly());
    Mockito.when(
            uploadedFileRecordService.getByEntityAndFileType(
                Mockito.eq(EntityType.PICKUP),
                Mockito.anyString(),
                Mockito.eq(FileTypes.BANK_TRANSFER)))
        .thenReturn(Collections.singletonList(new UploadedFileRecord()));
    Mockito.when(
            zoomTicketingAPIClientService.getByEntityInAndType(
                Mockito.anyList(), Mockito.anyString()))
        .thenReturn(Collections.emptyList());
    expectedException.expect(ZoomException.class);
    bankTransferService.createTicket(getMockMetadataInput());
    Mockito.when(
            zoomTicketingAPIClientService.getByEntityInAndType(
                Mockito.anyList(), Mockito.anyString()))
        .thenReturn(Collections.singletonList(ticketDTO));
    expectedException.expect(ZoomException.class);
    bankTransferService.createTicket(getMockMetadataInput());
    Mockito.verify(zoomTicketingAPIClientService, Mockito.times(1)).createTicket(Mockito.any());
  }

  @Test(expected = ZoomException.class)
  public void shouldFailSinceUploadedFileNotPresent() {
    Mockito.when(consignmentReadOnlyService.findRequiredById(Mockito.anyLong()))
        .thenReturn(new ConsignmentReadOnly());
    bankTransferService.createTicket(getMockMetadataInput());
  }

  private Map<String, String> getMockMetadataInput() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name(), "1");
    metadata.put(
        ZoomCommunicationFieldNames.PaymentDetails.BANK_NAME.name(),
        RandomStringUtils.randomAlphanumeric(5));
    metadata.put(
        ZoomCommunicationFieldNames.PaymentDetails.TOTAL_AMOUNT.name(),
        RandomStringUtils.randomNumeric(5));
    metadata.put(
        ZoomCommunicationFieldNames.PaymentDetails.TRANSACTION_REFERENCE_NO.name(),
        RandomStringUtils.randomAlphanumeric(5));
    metadata.put(
        ZoomCommunicationFieldNames.PaymentDetails.TRANSFERRED_AMOUNT.name(),
        RandomStringUtils.randomNumeric(5));
    return metadata;
  }

  @Test
  public void handleUniqueTransactionReferencePostingEventStatusCompleteTest() throws IOException {
    String utrNo = "1234567123456789";
    UniqueTransactionReferencePostingDTO uniqueTransactionReferencePostingDTO =
        new UniqueTransactionReferencePostingDTO();
    uniqueTransactionReferencePostingDTO.setUniqueTransactionReferenceNumber(utrNo);
    uniqueTransactionReferencePostingDTO.setStatus(
        UniqueTransactionReferencePostingStatus.COMPLETE);
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.UNIQUE_TRANSACTION_REFERENCE_POSTING);
    eventPayload.setPayload(
        "{\"uniqueTransactionReferenceNumber\":\"1234567123456789\",\"status\":\"COMPLETE\"}");
    Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any()))
        .thenReturn(uniqueTransactionReferencePostingDTO);
    bankTransferService.handleUniqueTransactionReferencePostingEvent(eventPayload.toString());
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1)).knockOffUtrBankTransfer(utrNo);
  }

  @Test
  public void handleUniqueTransactionReferencePostingEventStatusIncompleteTest()
      throws IOException {
    String utrNo = "1234567123456789";
    UniqueTransactionReferencePostingDTO uniqueTransactionReferencePostingDTO =
        new UniqueTransactionReferencePostingDTO();
    uniqueTransactionReferencePostingDTO.setUniqueTransactionReferenceNumber(utrNo);
    uniqueTransactionReferencePostingDTO.setStatus(
        UniqueTransactionReferencePostingStatus.INCOMPLETE);
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.UNIQUE_TRANSACTION_REFERENCE_POSTING);
    eventPayload.setPayload(
        "{\"uniqueTransactionReferenceNumber\":\"1234567123456789\",\"status\":\"INCOMPLETE\"}");
    Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any()))
        .thenReturn(uniqueTransactionReferencePostingDTO);
    bankTransferService.handleUniqueTransactionReferencePostingEvent(eventPayload.toString());
    Mockito.verify(zoomBackendAPIClientService, Mockito.times(1))
        .revertKnockOffUtrBankTransfer(utrNo);
  }
}
