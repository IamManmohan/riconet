package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.UploadedFileRecordService;
import com.rivigo.riconet.core.service.impl.BankTransferServiceImpl;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BankTransferServiceImplTest {

  @Mock private TicketingService ticketingService;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Mock private UploadedFileRecordService uploadedFileRecordService;

  @InjectMocks private BankTransferServiceImpl bankTransferService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createTicketWhenUploadedFilePresentForCn() {
    Mockito.when(consignmentReadOnlyService.findRequiredById(Mockito.anyLong()))
        .thenReturn(new ConsignmentReadOnly());
    Mockito.when(
            uploadedFileRecordService.getByEntityAndFileType(
                Mockito.eq(EntityType.CONSIGNMENT),
                Mockito.anyString(),
                Mockito.eq(FileTypes.BANK_TRANSFER)))
        .thenReturn(Collections.singletonList(new UploadedFileRecord()));
    bankTransferService.createTicket(getMockMetadataInput());

    Mockito.verify(ticketingService, Mockito.atLeastOnce()).createTicket(Mockito.any());
  }

  @Test
  public void createTicketWhenUploadedFilePresentForPickup() {
    Mockito.when(consignmentReadOnlyService.findRequiredById(Mockito.anyLong()))
        .thenReturn(new ConsignmentReadOnly());
    Mockito.when(
            uploadedFileRecordService.getByEntityAndFileType(
                Mockito.eq(EntityType.PICKUP),
                Mockito.anyString(),
                Mockito.eq(FileTypes.BANK_TRANSFER)))
        .thenReturn(Collections.singletonList(new UploadedFileRecord()));
    bankTransferService.createTicket(getMockMetadataInput());

    Mockito.verify(ticketingService, Mockito.atLeastOnce()).createTicket(Mockito.any());
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
}
