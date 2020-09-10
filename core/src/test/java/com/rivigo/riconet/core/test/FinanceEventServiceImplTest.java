package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.FinanceEventServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.vms.enums.ExpenseType;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FinanceEventServiceImplTest {

  @InjectMocks FinanceEventServiceImpl financeEventServiceImpl;

  @Mock FeederVendorService feederVendorService;

  @Mock ZoomPropertyService zoomPropertyService;

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
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    Mockito.when(feederVendorService.createFeederVendor(eventPayload.getPayload()))
        .thenReturn(jsonNode);
    Mockito.when(zoomPropertyService.getBoolean(ZoomPropertyName.EMAIL_ENABLED, false))
        .thenReturn(true);
    financeEventServiceImpl.processFinanceEvents(eventPayload);
    Assert.assertNotNull(jsonNode);
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
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    Mockito.when(feederVendorService.createFeederVendor(eventPayload.getPayload()))
        .thenReturn(jsonNode);
    financeEventServiceImpl.processFinanceEvents(eventPayload);
    Assert.assertNotNull(jsonNode);
  }
}
