package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.PaymentDetailV2Service;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.service.impl.TicketActionFactoryImpl;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TicketActionFactoryImplTest {

  @Mock private TicketingService ticketingService;

  @Mock private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Mock private ConsignmentService consignmentService;

  @Mock private PaymentDetailV2Service paymentDetailV2Service;

  @InjectMocks private TicketActionFactoryImpl ticketActionFactory;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldPerformActionOnAllChildTickets() {
    Mockito.when(ticketingService.getRequiredById(Mockito.anyLong()))
        .thenReturn(
            TicketDTO.builder()
                .typeId(ZoomTicketingConstant.PICKUP_BANK_TRANSFER_TICKET_TYPE_ID)
                .build());

    Mockito.when(consignmentReadOnlyService.findByPickupId(Mockito.anyLong()))
        .thenReturn(Collections.singletonList(new ConsignmentReadOnly()));

    Mockito.when(
            zoomTicketingAPIClientService.getByEntityInAndType(
                Mockito.anyListOf(String.class), Mockito.anyString()))
        .thenReturn(Arrays.asList(new TicketDTO(), new TicketDTO()));
    ticketActionFactory.consume(
        1L,
        "1",
        ZoomTicketingConstant.PICKUP_BANK_TRANSFER_ACTION_NAME,
        ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE);

    Mockito.verify(zoomTicketingAPIClientService, Mockito.atLeastOnce())
        .performAction(Mockito.any());

    Mockito.verify(ticketingService).closeTicketIfRequired(Mockito.any(), Mockito.anyString());
  }

  @Test
  public void shouldKnockOffWhenTicketIsApproved() {
    Mockito.when(ticketingService.getRequiredById(Mockito.anyLong()))
        .thenReturn(
            TicketDTO.builder().typeId(ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID).build());

    Mockito.when(paymentDetailV2Service.getByConsignmentId(Mockito.any()))
        .thenReturn(
            PaymentDetailV2.builder()
                .paymentType(PaymentType.BANK_TRANSFER)
                .transactionReferenceNo(RandomStringUtils.randomAlphanumeric(10))
                .bankAccountReference(RandomStringUtils.randomNumeric(3))
                .bankName(RandomStringUtils.randomAlphabetic(10))
                .build());
    ticketActionFactory.consume(
        1L,
        "1",
        ZoomTicketingConstant.BANK_TRANSFER_ACTION_NAME,
        ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE);

    Mockito.verify(zoomBackendAPIClientService)
        .handleKnockOffRequestForCnote(Mockito.anyString(), Mockito.any());

    Mockito.verify(zoomBackendAPIClientService, Mockito.never()).markRecoveryPending(Mockito.any());
  }

  @Test
  public void shouldTriggerRecoveryPendingWhenTicketIsRejected() {
    Mockito.when(ticketingService.getRequiredById(Mockito.anyLong()))
        .thenReturn(
            TicketDTO.builder().typeId(ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID).build());

    Mockito.when(paymentDetailV2Service.getByConsignmentId(Mockito.any()))
        .thenReturn(
            PaymentDetailV2.builder()
                .paymentType(PaymentType.BANK_TRANSFER)
                .transactionReferenceNo(RandomStringUtils.randomAlphanumeric(10))
                .bankAccountReference(RandomStringUtils.randomNumeric(3))
                .bankName(RandomStringUtils.randomAlphabetic(10))
                .totalAmount(BigDecimal.TEN)
                .build());
    ticketActionFactory.consume(1L, "1", ZoomTicketingConstant.BANK_TRANSFER_ACTION_NAME, "reject");

    Mockito.verify(zoomBackendAPIClientService, Mockito.never())
        .handleKnockOffRequestForCnote(Mockito.anyString(), Mockito.any());

    Mockito.verify(zoomBackendAPIClientService).markRecoveryPending(Mockito.any());
  }

  @Test
  public void shouldConsumeHandoverTicketWriteOffReject() {
    Mockito.when(ticketingService.getRequiredById(Mockito.anyLong()))
        .thenReturn(TicketDTO.builder().typeId(ZoomTicketingConstant.WRITEOFF_TYPE_ID).build());

    ticketActionFactory.consume(1L, "1", ZoomTicketingConstant.WRITE_OFF_ACTION_NAME, "reject");

    Mockito.verify(zoomBackendAPIClientService)
        .handleWriteOffApproveRejectRequest(Mockito.anyString(), Mockito.any());

    Mockito.verify(ticketingService).closeTicketIfRequired(Mockito.any(), Mockito.anyString());
  }

  @Test
  public void shouldConsumeHandoverTicketWriteOffAccept() {
    Mockito.when(ticketingService.getRequiredById(Mockito.anyLong()))
        .thenReturn(TicketDTO.builder().typeId(ZoomTicketingConstant.WRITEOFF_TYPE_ID).build());

    ticketActionFactory.consume(
        1L,
        "1",
        ZoomTicketingConstant.WRITE_OFF_ACTION_NAME,
        ZoomTicketingConstant.TICKET_ACTION_VALUE_APPROVE);

    Mockito.verify(zoomBackendAPIClientService)
        .handleWriteOffApproveRejectRequest(Mockito.anyString(), Mockito.any());

    Mockito.verify(ticketingService).closeTicketIfRequired(Mockito.any(), Mockito.anyString());
  }
}
