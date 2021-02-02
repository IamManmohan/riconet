package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.impl.ApiClientServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class ApiClientServiceTest {

  @InjectMocks private ApiClientServiceImpl apiClientService;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private ObjectMapper objectMapper;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    objectMapper = new ObjectMapper();
    org.springframework.test.util.ReflectionTestUtils.setField(
        apiClientService, "objectMapper", objectMapper);
  }

  @Test
  public void parseResponseJsonNodeFromDatastoreSuccessTest() throws IOException {

    JsonNode jsonNode = ApiServiceUtils.getDatastoreSuccessResponseSampleJsonNode();
    Boolean response =
        apiClientService.parseNewResponseJsonNode(
            jsonNode, objectMapper.constructType(Boolean.class));
    Assert.assertTrue(response);
  }

  @Test
  public void parseResponseJsonNodeFromDatastoreFailureTest() throws IOException {

    JsonNode jsonNode = ApiServiceUtils.getDatastoreFailureResponseSampleJsonNode();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while cleanup from datastore");
    apiClientService.parseNewResponseJsonNode(jsonNode, objectMapper.constructType(Boolean.class));
  }
}
