package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.compass.vendorcontractapi.dto.zoom.VendorContractZoomEventDTO;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.FeederVendorServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomBackendAPIClientServiceImpl;
import com.rivigo.vms.enums.ExpenseType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.BusinessPartnerRepository;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FeederVendorServiceImplTest {

  @InjectMocks FeederVendorServiceImpl feederVendorServiceImpl;

  @Mock ZoomBackendAPIClientServiceImpl zoomBackendAPIClientServiceImpl;

  @Mock ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock FeederVendorService feederVendorService;

  @Mock ObjectMapper objectMapper;

  @Mock FeederVendorRepository feederVendorRepository;

  @Mock BusinessPartnerRepository businessPartnerRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processVendorOnboardingEventTest() throws IOException {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = new VendorContractZoomEventDTO();
    vendorContractZoomEventDTO.setExpenseType(ExpenseType.RLH_FEEDER);
    vendorContractZoomEventDTO.setVendorCode("V-1001");
    vendorContractZoomEventDTO.setLegalEntityName("NEW VENDOR");
    EventPayload eventPayload = new EventPayload();
    eventPayload.setEventType(ZoomEventType.VENDOR_ACTIVE_EVENT);
    eventPayload.setPayload(
        "{\\\"vendorCode\\\":\\\"V-BP0019\\\",\\\"legalEntityName\\\":\\\"W Vendor\\\",\\\"expenseType\\\":\\\"RP\\\"}\"}");
    Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any()))
        .thenReturn(vendorContractZoomEventDTO);
    Mockito.when(feederVendorService.createFeederVendor(eventPayload.getPayload()))
        .thenReturn(null);
    feederVendorServiceImpl.processVendorOnboardingEvent(eventPayload);
  }

  @Test
  public void createFeederVendorTest() throws IOException {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = new VendorContractZoomEventDTO();
    vendorContractZoomEventDTO.setExpenseType(ExpenseType.RLH_FEEDER);
    vendorContractZoomEventDTO.setVendorCode("V-1001");
    vendorContractZoomEventDTO.setLegalEntityName("NEW VENDOR");
    String s =
        "{\"vendorCode\":\"V-BP0019\",\"legalEntityName\":\"W Vendor\",\"expenseType\":\"RP\"}\"}";
    Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any()))
        .thenReturn(vendorContractZoomEventDTO);
    Mockito.when(feederVendorRepository.findByVendorCode(Mockito.any())).thenReturn(null);
    Mockito.when(zoomBackendAPIClientServiceImpl.addFeederVendor(Mockito.any(), Mockito.any()))
        .thenReturn(null);
    feederVendorServiceImpl.createFeederVendor(s);
  }

  @Test
  public void createBPTest() throws IOException {
    VendorContractZoomEventDTO vendorContractZoomEventDTO = new VendorContractZoomEventDTO();
    vendorContractZoomEventDTO.setExpenseType(ExpenseType.BP);
    vendorContractZoomEventDTO.setVendorCode("V-1001");
    vendorContractZoomEventDTO.setLegalEntityName("NEW VENDOR");
    String s =
        "{\"vendorCode\":\"V-BP0019\",\"legalEntityName\":\"W Vendor\",\"expenseType\":\"BP\"}\"}";
    Mockito.when(objectMapper.readValue(Mockito.anyString(), (Class<Object>) Mockito.any()))
        .thenReturn(vendorContractZoomEventDTO);
    Mockito.when(businessPartnerRepository.findByCode((Mockito.any()))).thenReturn(null);
    Mockito.when(zoomBackendAPIClientServiceImpl.addBusinessPartner(Mockito.any()))
        .thenReturn(null);
    feederVendorServiceImpl.createFeederVendor(s);
  }

  @Test
  public void addFeederVendor() {
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode("V111");
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus(OperationalStatus.ACTIVE);
    dto.setLegalName("LEGAL");
    Mockito.when(zoomBackendAPIClientService.addFeederVendor(Mockito.any(), Mockito.any()))
        .thenReturn(null);
    zoomBackendAPIClientServiceImpl.addFeederVendor(Mockito.any(), Mockito.any());
  }

  @Test
  public void addBusinessPartner() {
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode("V111");
    dto.setType("BP");
    dto.setStatus("ACTIVE");
    dto.setLegalName("LEGAL");
    zoomBackendAPIClientServiceImpl.addBusinessPartner(dto);
  }
}
