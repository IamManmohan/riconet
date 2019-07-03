package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.impl.ZoomBackendAPIClientServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import com.rivigo.zoom.common.enums.PriorityReasonType;
import com.rivigo.zoom.exceptions.ZoomException;
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
  public void updateQcCheckTest() throws IOException {
    Boolean qcCheck = true;
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.updateQcCheck(consignmentId, qcCheck);
    validateReturnedData(jsonNode, HttpMethod.PUT, true);
  }

  @Test
  public void setPriorityMappingTest() throws IOException {
    String cnote = "1234567890";
    PriorityReasonType reasonType = PriorityReasonType.TICKET;
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.setPriorityMapping(cnote, reasonType);
  }

  @Test
  public void updateQcCheckExceptionTest() throws IOException {
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        "Error while marking consignment qcCheck needed  with consignmentId: 1234");
    zoomBackendAPIClientServiceImpl.updateQcCheck(consignmentId, true);
  }

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
  public void handleQcBlockerClosureTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomBackendAPIClientServiceImpl.handleQcBlockerClosure(5l);
    validateReturnedData(jsonNode, HttpMethod.PUT, false);
  }

  @Test
  public void handleQcBlockerClosureExceptionTest() throws IOException {
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        "Error while handling QcBlocker ticket closure with ticketId: 5");
    zoomBackendAPIClientServiceImpl.handleQcBlockerClosure(5l);
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
        .parseJsonNode(jsonNodeArgumentCaptor.capture(), Mockito.any());
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
        .thenThrow(new IOException());
  }
}
