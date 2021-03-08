package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.impl.ZoomBackendAPIClientServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

/** Created by aditya on 3/5/18. */
public class ZoomBackendAPIClientServiceTest {

  @InjectMocks private ZoomBackendAPIClientServiceImpl zoomBackendAPIClientServiceImpl;

  @Mock private ApiClientService apiClientService;

  @Captor private ArgumentCaptor<JsonNode> jsonNodeArgumentCaptor;

  @Captor private ArgumentCaptor<MultiValueMap<String, String>> multiValueMapArgumentCaptor;

  @Captor private ArgumentCaptor<HttpMethod> httpMethodArgumentCaptor;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  private Long consignmentId = 1234L;

  @Test
  public void recalculateCpdOfBfTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.recalculateCpdOfBf(consignmentId);
    validateReturnedData(jsonNode, HttpMethod.PUT, true);
  }

  @Test
  public void recalculateCpdOfBfExceptionTest() throws IOException {
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        "Error while recalculating cpd of BF consignment with id: 1234");
    zoomBackendAPIClientServiceImpl.recalculateCpdOfBf(consignmentId);
  }

  @Test
  public void handleConsignmentBlockerTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.handleConsignmentBlocker(
        ConsignmentBlockerRequestDTO.builder()
            .isActive(true)
            .reason("reason")
            .subReason("subreason")
            .consignmentId(5l)
            .requestType(ConsignmentBlockerRequestType.BLOCK)
            .build());
    validateReturnedData(jsonNode, HttpMethod.POST, false);
  }

  @Test
  public void handleConsignmentBlockerExceptionTest() throws IOException {
    mockApiClientServiceGetEntityException();
    ConsignmentBlockerRequestDTO blocker =
        ConsignmentBlockerRequestDTO.builder()
            .isActive(true)
            .reason("reason")
            .subReason("subreason")
            .consignmentId(5l)
            .requestType(ConsignmentBlockerRequestType.BLOCK)
            .build();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        "Error while handling consignmentBlocker " + blocker.toString());
    zoomBackendAPIClientServiceImpl.handleConsignmentBlocker(blocker);
  }

  @Test()
  public void deletePickupTest1() throws IOException {
    Long pickupId = 1L;
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.deletePickup(pickupId);
  }

  @Test()
  public void deletePickupTest2() throws IOException {
    Long pickupId = 1L;
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.deletePickup(pickupId);
  }

  private void validateReturnedData(
      JsonNode jsonNode, HttpMethod httpMethod, Boolean verifyConsignmentId) throws IOException {
    verify(apiClientService, times(1))
        .getEntity(
            Mockito.any(),
            httpMethodArgumentCaptor.capture(),
            Mockito.anyString(),
            multiValueMapArgumentCaptor.capture(),
            Mockito.any());
    Assert.assertEquals(httpMethod, httpMethodArgumentCaptor.getValue());
    if (verifyConsignmentId)
      Assert.assertEquals(
          consignmentId.toString(),
          multiValueMapArgumentCaptor.getValue().get("consignmentId").get(0));

    verify(apiClientService, times(1))
        .parseNewResponseJsonNode(jsonNodeArgumentCaptor.capture(), Mockito.any());
    Assert.assertEquals(jsonNode, jsonNodeArgumentCaptor.getValue());
  }

  private void mockApiClientServiceGetEntity(JsonNode jsonNode) throws IOException {
    Mockito.when(
            apiClientService.getEntity(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(jsonNode);
  }

  private void mockApiClientServiceGetEntityException() throws IOException {
    Mockito.when(
            apiClientService.getEntity(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenThrow(new IOException("Test Exception"));
  }

  @Test
  public void addBusinessPartnerTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode("V111");
    dto.setType("BP");
    dto.setStatus("ACTIVE");
    dto.setLegalName("LEGAL");
    Mockito.when(
            apiClientService.getEntity(
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject()))
        .thenReturn(jsonNode);
    JsonNode actual_jsonNode = zoomBackendAPIClientServiceImpl.addBusinessPartner(dto);
    Assert.assertEquals(jsonNode, actual_jsonNode);
  }

  @Test
  public void addBusinessPartnerExceptionTest() throws IOException {

    BusinessPartnerDTO dto = new BusinessPartnerDTO();
    dto.setCode("V111");
    dto.setType("BP");
    dto.setStatus("ACTIVE");
    dto.setLegalName("LEGAL");
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while creating BP with dto");
    zoomBackendAPIClientServiceImpl.addBusinessPartner(dto);
  }

  @Test
  public void addFeederVendorTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode("V111");
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus(OperationalStatus.ACTIVE);
    dto.setLegalName("LEGAL");
    Mockito.when(
            apiClientService.getEntity(
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject(),
                Mockito.anyObject()))
        .thenReturn(jsonNode);
    JsonNode actual_jsonNode = zoomBackendAPIClientServiceImpl.addFeederVendor(dto);
    Assert.assertEquals(jsonNode, actual_jsonNode);
  }

  @Test
  public void addFeederVendorExceptionTest() throws IOException {
    FeederVendorDTO dto = new FeederVendorDTO();
    dto.setVendorCode("V111");
    dto.setVendorType(FeederVendor.VendorType.VENDOR);
    dto.setVendorStatus(OperationalStatus.ACTIVE);
    dto.setLegalName("LEGAL");
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while creating vendor with dto");
    zoomBackendAPIClientServiceImpl.addFeederVendor(dto);
  }

  @Test
  public void startDemurrageTest() throws IOException {
    String cnote = "123456";
    String startTime = "1234567890";
    String id = "654321";
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.startDemurrage(cnote, startTime, id);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.startDemurrage(cnote, startTime, id);
  }

  @Test
  public void endDemurrageTest() throws IOException {
    String cnote = "123456";
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.endDemurrage(cnote);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.endDemurrage(cnote);
  }

  @Test
  public void cancelDemurrageTest() throws IOException {
    String cnote = "123456";
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.cancelDemurrage(cnote);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.cancelDemurrage(cnote);
  }

  @Test
  public void retriggerCpdCalculationsForHolidayTest() throws IOException {
    HolidayV2Dto holidayV2Dto = new HolidayV2Dto();
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.retriggerCpdCalculationsForHoliday(holidayV2Dto);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.retriggerCpdCalculationsForHoliday(holidayV2Dto);
  }

  @Test
  public void knockOffUtrBankTransferTest() throws IOException {
    String utrNo = "1234567123456789";
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.knockOffUtrBankTransfer(utrNo);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.knockOffUtrBankTransfer(utrNo);
  }

  @Test
  public void revertKnockOffUtrBankTransferTest() throws IOException {
    String utrNo = "1234567123456789";
    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.revertKnockOffUtrBankTransfer(utrNo);
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    zoomBackendAPIClientServiceImpl.revertKnockOffUtrBankTransfer(utrNo);
  }
}
