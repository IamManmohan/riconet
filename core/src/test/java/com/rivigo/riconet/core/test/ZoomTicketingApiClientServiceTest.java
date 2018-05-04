package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomTicketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomTicketing.LocationType;
import com.rivigo.riconet.core.enums.zoomTicketing.TicketStatus;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.ZoomTicketingAPIClientServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.web.client.RestTemplate;


@Slf4j
public class ZoomTicketingApiClientServiceTest {

  @InjectMocks
  private ZoomTicketingAPIClientServiceImpl zoomTicketingAPIClientService;

  @Mock
  private ApiClientService apiClientService;

  @Captor
  private ArgumentCaptor<JsonNode> jsonNodeArgumentCaptor;

  @Captor
  private ArgumentCaptor<MultiValueMap<String, String>> multiValueMapArgumentCaptor;

  @Captor
  private ArgumentCaptor<HttpMethod> httpMethodArgumentCaptor;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  private String cnote = "1234567890";
  private List<String> typeId = Arrays
      .asList(ZoomTicketingConstant.QC_RECHECK_TYPE_ID.toString(),
          ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString());

  @Test
  public void getTicketsByCnoteNullCheck() {
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Please provide a valid cnote");
    zoomTicketingAPIClientService.getTicketsByCnoteAndType(null,null);
  }

  @Test
  public void getTicketsByCnoteAndTypeTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomTicketingAPIClientService.getTicketsByCnoteAndType(cnote,typeId);
    validateReturnedData(HttpMethod.GET);
    Assert.assertEquals(cnote, multiValueMapArgumentCaptor.getValue().get("entityId").get(0));
    Assert.assertEquals(typeId, multiValueMapArgumentCaptor.getValue().get("typeId"));
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void getTicketsByCnoteAndTypeExceptionTest() throws IOException {
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while getting qc tickets with cnote 1234567890");
    zoomTicketingAPIClientService.getTicketsByCnoteAndType(cnote,typeId);
  }

  @Test
  public void createTicketTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    TicketDTO ticketDTO = getTicketDTO();
    zoomTicketingAPIClientService.createTicket(ticketDTO);
    validateReturnedData(HttpMethod.POST);
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void createTicketExceptionTest() throws IOException {
    TicketDTO ticketDTO = getTicketDTO();
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while creating qc tickets with cnote 1234567890");
    zoomTicketingAPIClientService.createTicket(ticketDTO);
  }

  @Test
  public void editTicketTest() throws IOException {
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    TicketDTO ticketDTO = getTicketDTO();
    zoomTicketingAPIClientService.editTicket(ticketDTO);
    validateReturnedData(HttpMethod.PUT);
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void editTicketExceptionTest() throws IOException {
    TicketDTO ticketDTO = getTicketDTO();
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while editing qc tickets with cnote 1234567890");
    zoomTicketingAPIClientService.editTicket(ticketDTO);
  }

  @Test
  public void makeCommentTest() throws IOException {
    Long ticketId = 1234L;
    String comment = "test comment";
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomTicketingAPIClientService.makeComment(ticketId, comment);
    validateReturnedData(HttpMethod.POST);
    Assert.assertEquals(ticketId.toString(), multiValueMapArgumentCaptor.getValue().get("ticketId").get(0));
    Assert.assertEquals(comment, multiValueMapArgumentCaptor.getValue().get("text").get(0));
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void makeCommentExceptionTest() throws IOException {
    Long ticketId = 1234L;
    String comment = "test comment";
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while making comments for ticket with id 1234");
    zoomTicketingAPIClientService.makeComment(ticketId, comment);
  }

  @Test
  public void getGroupIdTest() throws IOException {
    Long locationId = 1200L;
    String groupName = "Test group name";
    LocationType locationType = LocationType.OU;
    JsonNode jsonNode = ApiServiceUtils.getSampleJsonNode();
    mockApiClientServiceGetEntity(jsonNode);
    zoomTicketingAPIClientService.getGroupId(locationId, groupName, locationType);
    validateReturnedData(HttpMethod.GET);
    Assert.assertEquals(locationId.toString(), multiValueMapArgumentCaptor.getValue().get("locationId").get(0));
    Assert.assertEquals(groupName, multiValueMapArgumentCaptor.getValue().get("groupName").get(0));
    Assert.assertEquals(locationType.name(), multiValueMapArgumentCaptor.getValue().get("locationType").get(0));
    validateParsingCallDone(jsonNode);
  }

  @Test
  public void getGroupIdExceptionTest() throws IOException {
    Long locationId = 1200L;
    String groupName = "Test group name";
    LocationType locationType = LocationType.OU;
    mockApiClientServiceGetEntityException();
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Error while getting groupId for locationId: 1200 and groupName: Test group name locationType: OU");
    zoomTicketingAPIClientService.getGroupId(locationId, groupName, locationType);
  }

  private void validateReturnedData(HttpMethod httpMethod) throws IOException {
    verify(apiClientService,times(1))
        .getEntity(Mockito.any(), httpMethodArgumentCaptor.capture(), Mockito.anyString(),
            multiValueMapArgumentCaptor.capture(), Mockito.any());
    Assert.assertEquals(httpMethod, httpMethodArgumentCaptor.getValue());
  }

  private void validateParsingCallDone(JsonNode jsonNode) {
    verify(apiClientService,times(1))
        .parseJsonNode(jsonNodeArgumentCaptor.capture(), Mockito.any());
    Assert.assertEquals(jsonNode, jsonNodeArgumentCaptor.getValue());
  }


  private void mockApiClientServiceGetEntity(JsonNode jsonNode) throws IOException {
  Mockito.when(apiClientService.getEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
      .thenReturn(jsonNode);
}

  private void mockApiClientServiceGetEntityException() throws IOException {
    Mockito.when(apiClientService.getEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenThrow(new IOException());
  }

  private TicketDTO getTicketDTO() {
    return TicketDTO.builder()
        .typeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID)
        .status(TicketStatus.CLOSED)
        .entityId(cnote)
        .id(1L)
        .build();
  }


}