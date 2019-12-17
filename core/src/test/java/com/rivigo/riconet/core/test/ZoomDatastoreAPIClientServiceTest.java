package com.rivigo.riconet.core.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.datastore.EwaybillMetadataDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.impl.ZoomDatastoreAPIClientServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class ZoomDatastoreAPIClientServiceTest {

  @InjectMocks private ZoomDatastoreAPIClientServiceImpl zoomDatastoreAPIClientService;

  @Mock private ApiClientService apiClientService;

  @Captor private ArgumentCaptor<JsonNode> jsonNodeArgumentCaptor;

  @Captor private ArgumentCaptor<MultiValueMap<String, String>> multiValueMapArgumentCaptor;

  @Captor private ArgumentCaptor<HttpMethod> httpMethodArgumentCaptor;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ObjectMapper objectMapper = new ObjectMapper();
    org.springframework.test.util.ReflectionTestUtils.setField(
        zoomDatastoreAPIClientService, "objectMapper", objectMapper);
  }

  @Test
  public void cleanupAddressesUsingEwaybillMetadata() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    mockApiClientServiceParseResponseJsonNodeFromDatastore();
    EwaybillMetadataDTO ewaybillMetadataDTO = new EwaybillMetadataDTO();
    ewaybillMetadataDTO.setEwaybillNumber("112233445566");
    zoomDatastoreAPIClientService.cleanupAddressesUsingEwaybillMetadata(ewaybillMetadataDTO);
    validateReturnedData(HttpMethod.POST);
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void cleanupAddressesUsingEwaybillMetadataExceptionTest() throws IOException {
    EwaybillMetadataDTO ewaybillMetadataDTO = new EwaybillMetadataDTO();
    ewaybillMetadataDTO.setEwaybillNumber("112233445566");
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage(
        "Error while doing cleanup from ewaybill metadata from ewaybill 112233445566");
    zoomDatastoreAPIClientService.cleanupAddressesUsingEwaybillMetadata(ewaybillMetadataDTO);
  }

  private void validateReturnedData(HttpMethod httpMethod) throws IOException {
    verify(apiClientService, times(1))
        .getEntity(
            Mockito.any(),
            httpMethodArgumentCaptor.capture(),
            Mockito.anyString(),
            multiValueMapArgumentCaptor.capture(),
            Mockito.any());
    Assert.assertEquals(httpMethod, httpMethodArgumentCaptor.getValue());
  }

  private void validateParsingCallDone(JsonNode jsonNode) {
    verify(apiClientService, times(1))
        .parseResponseJsonNodeFromDatastore(jsonNodeArgumentCaptor.capture(), Mockito.any());
    Assert.assertEquals(jsonNode, jsonNodeArgumentCaptor.getValue());
  }

  private void mockApiClientServiceParseResponseJsonNodeFromDatastore() {
    when(apiClientService.parseResponseJsonNodeFromDatastore(any(), any())).thenReturn(true);
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
}
