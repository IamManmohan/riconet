package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ClientContactDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.CMSService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class CMSServiceImpl implements CMSService {

  @Autowired ObjectMapper objectMapper;

  private static final String CLIENT_CODE_STRING = "code";

  private static final String CLIENT_CODE_CONTACTS_PATH = "/response/0/contacts";

  private static final String CLIENT_DETAIL_ENDPOINT = "/clients/client-details";

  @Autowired ApiClientService apiClientService;

  @Value("${cms.base.url}")
  private String cmsBaseURL;

  @Override
  public List<ClientContactDTO> getClientContacts(String clientCode) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(CLIENT_CODE_STRING, Collections.singletonList(clientCode));
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              null, HttpMethod.GET, CLIENT_DETAIL_ENDPOINT, params, cmsBaseURL);
      log.debug("Clients Details api response: {}", responseJson);
      return objectMapper.convertValue(
          responseJson.at(CLIENT_CODE_CONTACTS_PATH),
          objectMapper
              .getTypeFactory()
              .constructCollectionType(List.class, ClientContactDTO.class));
    } catch (Exception e) {
      log.error(
          "Error while getting the client detail from cms for client {} {}",
          clientCode,
          e.getStackTrace());
      return new ArrayList<>();
    }
  }
}
