package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ZoomTicketingAPIClientServiceImpl implements ZoomTicketingAPIClientService {

  @Autowired private ObjectMapper objectMapper;

  @Value("${zoom.ticketing.url}")
  private String ticketingBaseUrl;

  @Autowired private ApiClientService apiClientService;

  @Override
  public List<TicketDTO> getTicketsByCnoteAndType(String cnote, List<String> typeId) {
    if (StringUtils.isEmpty(cnote)) {
      throw new ZoomException("Please provide a valid cnote");
    }
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("entityId", Collections.singletonList(cnote));
    valuesMap.put("typeId", typeId);
    String url = UrlConstant.ZOOM_TICKETING_GET_BY_CNOTE_AND_TYPE;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.GET, url, valuesMap, ticketingBaseUrl);
    } catch (IOException e) {
      log.error("Error while getting qc tickets with cnote {}", cnote, e);
      throw new ZoomException("Error while getting qc tickets with cnote " + cnote);
    }

    TypeReference<List<TicketDTO>> mapType = new TypeReference<List<TicketDTO>>() {};

    return (List<TicketDTO>) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public TicketDTO createTicket(TicketDTO ticketDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_TICKETING_POST_PUT_TICKET;
    try {
      responseJson =
          apiClientService.getEntity(ticketDTO, HttpMethod.POST, url, null, ticketingBaseUrl);
    } catch (IOException e) {
      log.error("Error while creating tickets with entity {}", ticketDTO.getEntityId(), e);
      throw new ZoomException(
          "Error while creating tickets with entity " + ticketDTO.getEntityId());
    }
    TypeReference<TicketDTO> mapType = new TypeReference<TicketDTO>() {};

    return (TicketDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  @Override
  public TicketDTO editTicket(TicketDTO ticketDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_TICKETING_POST_PUT_TICKET;
    try {
      responseJson =
          apiClientService.getEntity(ticketDTO, HttpMethod.PUT, url, null, ticketingBaseUrl);
    } catch (IOException e) {
      log.error("Error while editing qc tickets with cnote {}", ticketDTO.getEntityId(), e);
      throw new ZoomException(
          "Error while editing qc tickets with cnote " + ticketDTO.getEntityId());
    }
    TypeReference<TicketDTO> mapType = new TypeReference<TicketDTO>() {};

    return (TicketDTO) apiClientService.parseJsonNode(responseJson, mapType);
  }

  public void makeComment(Long ticketId, String comment) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("ticketId", Collections.singletonList(ticketId.toString()));
    valuesMap.put("text", Collections.singletonList(comment));
    String url = UrlConstant.ZOOM_TICKETING_POST_COMMENT;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.POST, url, valuesMap, ticketingBaseUrl);
    } catch (IOException e) {
      log.error("Error while making comments for ticket with id {}", ticketId, e);
      throw new ZoomException("Error while making comments for ticket with id " + ticketId);
    }

    apiClientService.parseJsonNode(responseJson, null);
  }

  @Override
  public GroupDTO getGroupId(Long locationId, String groupName, LocationType locationType) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("locationId", Collections.singletonList(locationId.toString()));
    valuesMap.put("groupName", Collections.singletonList(groupName));
    valuesMap.put("locationType", Collections.singletonList(locationType.name()));
    String url = UrlConstant.ZOOM_TICKETING_GET_GROUP_ID;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.GET, url, valuesMap, ticketingBaseUrl);
    } catch (IOException e) {
      log.info(
          "Error while getting groupId for locationId: {} and groupName: {} locationType: {} ",
          locationId,
          groupName,
          locationType,
          e);
      throw new ZoomException(
          "Error while getting groupId for locationId: "
              + locationId
              + " and groupName: "
              + groupName
              + " locationType: "
              + locationType);
    }
    TypeReference<GroupDTO> mapType = new TypeReference<GroupDTO>() {};
    return ((GroupDTO) apiClientService.parseJsonNode(responseJson, mapType));
  }
}
