package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.FinanceEventService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.FinanceEventServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomBackendAPIClientServiceImpl;
import com.rivigo.vms.enums.ExpenseType;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FinanceEventServiceImplTest {

  @InjectMocks FinanceEventServiceImpl financeEventServiceImpl;

  @Mock ZoomBackendAPIClientServiceImpl zoomBackendAPIClientServiceImpl;

  @Mock ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock FeederVendorService feederVendorService;

  @Mock ObjectMapper objectMapper;

  @Mock FeederVendorRepository feederVendorRepository;

  @Mock BusinessPartnerRepository businessPartnerRepository;

  @Mock FinanceEventService financeEventService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processFinanceEventsEventTest() throws IOException {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = new VendorContractZoomEventDTO();
    vendorContractZoomEventDTO.setExpenseType(ExpenseType.RLH_FEEDER);
    vendorContractZoomEventDTO.setVendorCode("V-1001");
    vendorContractZoomEventDTO.setLegalEntityName("NEW VENDOR");
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.VENDOR_ACTIVE_EVENT);
    eventPayload.setPayload(
        "{\\\"vendorCode\\\":\\\"V-BP0019\\\",\\\"legalEntityName\\\":\\\"W Vendor\\\",\\\"expenseType\\\":\\\"RP\\\"}\"}");
    Mockito.when(feederVendorService.createFeederVendor(eventPayload.getPayload()))
        .thenReturn(null);
    financeEventServiceImpl.processFinanceEvents(eventPayload);
  }

  @Test
  public void processFinanceEventsEventNothingTest() throws IOException {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = new VendorContractZoomEventDTO();
    vendorContractZoomEventDTO.setExpenseType(ExpenseType.RLH_FEEDER);
    vendorContractZoomEventDTO.setVendorCode("V-1001");
    vendorContractZoomEventDTO.setLegalEntityName("NEW VENDOR");
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.CMS_CLIENT_DEACTIVATE);
    eventPayload.setPayload(
        "{\\\"vendorCode\\\":\\\"V-BP0019\\\",\\\"legalEntityName\\\":\\\"W Vendor\\\",\\\"expenseType\\\":\\\"RP\\\"}\"}");
    Mockito.when(feederVendorService.createFeederVendor(eventPayload.getPayload()))
        .thenReturn(null);
    financeEventServiceImpl.processFinanceEvents(eventPayload);
  }
}
