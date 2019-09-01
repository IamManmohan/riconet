package com.rivigo.riconet.core.test;

import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.FeederVendorServiceImpl;
import com.rivigo.zoom.common.model.FeederVendor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

public class FeederVendorServiceImplTest {

  @InjectMocks FeederVendorServiceImpl feederVendorService;

  @Mock ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock FeederVendorServiceImpl feederVendorServiceImpl;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processVendorOnboardingEventTest() {
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.VENDOR_ACTIVE_EVENT);
    eventPayload.setPayload(
        "{\\\"vendorCode\\\":\\\"V-BP0019\\\",\\\"legalEntityName\\\":\\\"W Vendor\\\",\\\"expenseType\\\":\\\"RP\\\"}\"}");
    feederVendorServiceImpl.processVendorOnboardingEvent(eventPayload);
  }

  @Test
  public void createFeederVendorTest() {
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.VENDOR_ACTIVE_EVENT);
    eventPayload.setPayload(
        "{\\\"vendorCode\\\":\\\"V-BP0019\\\",\\\"legalEntityName\\\":\\\"W Vendor\\\",\\\"expenseType\\\":\\\"RP\\\"}\"}");
    feederVendorServiceImpl.createFeederVendor(eventPayload.getPayload());
  }

  @Test
  public void addFeederVendor() {
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode("V111");
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus("ACTIVE");
    dto.setLegalName("LEGAL");
    zoomBackendAPIClientService.addFeederVendor(dto, HttpMethod.POST);
  }

  @Test
  public void addBusinessPartner() {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode("V111");
    dto.setType("BP");
    dto.setStatus("ACTIVE");
    dto.setLegalName("LEGAL");
    zoomBackendAPIClientService.addBusinessPartner(dto);
  }
}
