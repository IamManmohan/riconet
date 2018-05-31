package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.zoombook.ZoomBookTransactionResponseDTO;
import com.rivigo.riconet.core.service.ZoomBookAPIClientService;
import com.rivigo.riconet.core.utils.FinanceUtils;
import com.rivigo.zoom.common.dto.zoombook.TransactionModelDTO;
import com.rivigo.zoom.common.dto.zoombook.ZoomBookTransactionRequestDTO;
import com.rivigo.zoom.common.repository.mysql.ZoomBookTransactionRecordRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ZoomBookAPIClientServiceImpl implements ZoomBookAPIClientService {

  private static final String FAILURE = "FAILURE";
  private static final String STATUS = "status";
  private static final String SUCCESS = "SUCCESS";
  private static final String CLIENT_TOKEN = "clientToken";
  private static final String CLIENT_KEY = "clientKey";
  private static final String RESPONSE = "response";
  private static final String STATUS_CODE = "statusCode";



  @Value("${zoom.zoombook.url}")
  private String zoomBookUrl;

  @Value("${zoombookClientKey}")
  private String zoombookClientKey;

  @Value("${zoombookClientSecret}")
  private String zoombookClientSecret;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ZoomBookTransactionRecordRepository zoomBookTransactionRecordRepository;

  private RestTemplate restTemplate = new RestTemplate();

  @Getter
  @Setter
  private class ZoomBookResponse<E> {
    Map<String, String> responseMap;
    List<E> zoomBookResponseList;
  }


  @Override
  public List<TransactionModelDTO> getEntityCollectionsSummary(
      Long orgId, String functionType, String tenantType, Long fromDateTime, Long toDateTime, Boolean getAllByReference) {
    String requestURL = "zoombook/transaction/compile";

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.set("orgId", String.valueOf(orgId));
    queryParams.set("tenantType", tenantType);
    queryParams.set("functionType", functionType);
    queryParams.set("getAllByReference", getAllByReference.toString());
    if (null != fromDateTime && null != toDateTime) {
      queryParams.set("fromDate", String.valueOf(fromDateTime));
      queryParams.set("toDate", String.valueOf(toDateTime));
    }
    TypeReference responseType = new TypeReference<List<TransactionModelDTO>>() {};
    Object response =
        getDataFromZoomBook(
            requestURL,
            queryParams,
            responseType,
            FinanceUtils.createToken(String.valueOf(orgId), functionType, tenantType, zoombookClientSecret));
    if (response == null) {
      return Collections.emptyList();
    }
    return (List<TransactionModelDTO>) response;
  }

  @Override
  public Object getDataFromZoomBook(
      String requestUrl, MultiValueMap<String, String> queryParams, TypeReference responseType, String zoombookClientToken) {

    Map<String, String> zoomBookReponse = getDataFromZoomBook(requestUrl, queryParams, zoombookClientToken);

    return getTransactionModelDetails(zoomBookReponse, responseType);
  }

  private Object getTransactionModelDetails(Map<String, String> zoomBookReponse, TypeReference responseType) {
    if (null == zoomBookReponse || zoomBookReponse.get(STATUS).equals(FAILURE)) {
      throw new ZoomException("Failed to get Collections Summary From ZoomBook");
    }
    ObjectMapper mapper = new ObjectMapper();
    Object responseDto;
    try {
      if (zoomBookReponse.get(RESPONSE) == null) {
        return null;
      }
      responseDto = mapper.readValue(zoomBookReponse.get(RESPONSE), responseType);
    } catch (IOException e) {
      log.error("Error while reading data from finance", e);
      throw new ZoomException("Error while reading data from finance ");
    }
    return responseDto;
  }

  private Map<String, String> getDataFromZoomBook(
      String requestUrl, MultiValueMap<String, String> queryParams, String zoombookClientToken) {
    Map<String, String> responseMap = new HashMap<>();
    try {
      log.info(" params {} ", queryParams);
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.add(CLIENT_KEY, zoombookClientKey);
      headers.add(CLIENT_TOKEN, zoombookClientToken);
      HttpEntity entity = new HttpEntity(headers);

      // Sample requestUrl : status
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(zoomBookUrl + "/" + requestUrl).queryParams(queryParams);

      log.debug("Calling API for to GET data from Zoom-book");
      ResponseEntity<JsonNode> oauthResponse =
          restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, JsonNode.class);
      JsonNode responseJson = oauthResponse.getBody();

      log.info("Completed API to get data from Zoom-book");

      String statusCode = responseJson.get(STATUS_CODE).toString();
      String status = responseJson.get(STATUS).toString();

      if (oauthResponse.getStatusCode() == HttpStatus.OK) {
        responseMap.put(STATUS, SUCCESS);
        responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());
        if (200 == Integer.valueOf(statusCode) && "\"SUCCESS\"".equals(status)) {
          responseMap.put(RESPONSE, responseJson.get(RESPONSE).toString());
          return responseMap;
        }
        if (700 == Integer.valueOf(statusCode)) {
          responseMap.put(RESPONSE, null);
          return responseMap;
        }
        if (801 == Integer.valueOf(statusCode)) {
          responseMap.put(RESPONSE, "[]");
          return responseMap;
        }
      }

      responseMap.put(STATUS, FAILURE);
      responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());
      responseMap.put(RESPONSE, null);
      log.error("  {} ", responseJson.get(RESPONSE).toString());
      return responseMap;

    } catch (Exception e) {
      log.error("Unknown exception while trying to get data from zoom-book for url {} with params: {}", requestUrl, queryParams.toString());
      throw new ZoomException("Unknown exception while trying to get data from zoom-book");
    }
  }

  @Override
  public Map<String, String> processZoomBookTransaction(
      List<ZoomBookTransactionRequestDTO> zoomBookTransactionRequestDTOList) {

    if (CollectionUtils.isEmpty(zoomBookTransactionRequestDTOList)) {
      throw new ZoomException("zoomBookTransactionRequestDTOList can not be empty or null");
    }
    Map<String, String> response = new HashMap<>();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    zoomBookTransactionRequestDTOList.forEach(dto -> {
      if (dto.getClientRequestId() == null) {
        dto.setClientRequestId(UUID.randomUUID().toString().replace("-", ""));
      }
    });
    ZoomBookResponse<ZoomBookTransactionResponseDTO> zoomBookTransactionResponse = zoomBookTransaction(
        zoomBookTransactionRequestDTOList, uuid);

    response.put(STATUS, FAILURE);
    response.put("uuid", uuid);

    Map<String, String> responseMap = zoomBookTransactionResponse.getResponseMap();
    if (responseMap == null || !SUCCESS.equals(responseMap.get(STATUS))) {
      throw new ZoomException("Exception from ZoomBook with status {} ", responseMap.get(STATUS));
    }
    Map<String, String> clientRequestIdtoTxnNumberMap = new HashMap<>();
    for (ZoomBookTransactionResponseDTO zoomBookTransactionResponseDTO : zoomBookTransactionResponse
        .getZoomBookResponseList()) {
      clientRequestIdtoTxnNumberMap.put(zoomBookTransactionResponseDTO.getClientRequestId(),
          zoomBookTransactionResponseDTO.getTxnNo());
    }

    response.put(STATUS, SUCCESS);
    return response;
  }

  private ZoomBookResponse<ZoomBookTransactionResponseDTO> zoomBookTransaction(List<ZoomBookTransactionRequestDTO> zoomBookTransactionRequestDTOList, String uuid){

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add(CLIENT_KEY, zoombookClientKey);
    String commaSeparatedOrgIds = StringUtils.join(
        zoomBookTransactionRequestDTOList
            .stream()
            .map(ZoomBookTransactionRequestDTO::getOrgId)
            .collect(Collectors.toList())
        , ",");
    String commaSeparatedClientRequestIds = StringUtils.join(
        zoomBookTransactionRequestDTOList
            .stream()
            .map(ZoomBookTransactionRequestDTO::getClientRequestId)
            .collect(Collectors.toList())
        , ",");
    String commaSeparatedFunctionTypes = StringUtils.join(
        zoomBookTransactionRequestDTOList
            .stream()
            .map(ZoomBookTransactionRequestDTO::getFunctionType)
            .collect(Collectors.toList())
        , ",");
    headers.add(CLIENT_TOKEN, FinanceUtils.createToken(commaSeparatedClientRequestIds,
        commaSeparatedOrgIds,
        commaSeparatedFunctionTypes,zoombookClientSecret));


    String requestJson ;

    try {
      requestJson = objectMapper.writeValueAsString(zoomBookTransactionRequestDTOList);
    } catch (JsonProcessingException e) {
      throw new ZoomException("Unable to write zoomBookTransactionRequestDTOList to string");
    }

    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(zoomBookUrl + UrlConstant.ZOOMBOOK_TRANSACTION_V2);

    TypeReference type=new TypeReference<List<ZoomBookTransactionResponseDTO>>(){};

    log.info("Calling API {} for zoomBookTransaction for uuid {}, requestJson {}", zoomBookUrl + UrlConstant.ZOOMBOOK_TRANSACTION_V2
        , uuid, requestJson);
    return callZoomBook(type,HttpMethod.POST,entity,builder);
  }

  private ZoomBookResponse callZoomBook(TypeReference type, HttpMethod httpMethod, HttpEntity entity,
      UriComponentsBuilder builder){
    ZoomBookResponse zoomBookResponse = new ZoomBookResponse<>();
    Map<String, String> responseMap = new HashMap<>();
    List<Object> obj=null;
    try {
      ResponseEntity<JsonNode> oauthResponse = restTemplate.exchange(builder.build().encode().toUri(), httpMethod, entity, JsonNode.class);
      JsonNode responseJson = oauthResponse.getBody();
      log.debug("Response from zoombook: {}", responseJson);

      String statusCode = responseJson.get(STATUS_CODE).toString();
      String status = responseJson.get(STATUS).toString();

      if (oauthResponse.getStatusCode() == HttpStatus.OK
          && 200 == Integer.valueOf(statusCode)
          && "\"SUCCESS\"".equals(status)) {
        obj = objectMapper.readValue(responseJson.get(RESPONSE).toString(),type);
        responseMap.put(STATUS, SUCCESS);
        responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());

        //add txn detail to audit transaction
      } else {
        responseMap.put(STATUS, FAILURE);
        responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());

      }
    }catch(Exception e){
      log.error(e.getMessage(), e);
      responseMap.put(STATUS, FAILURE);
    }
    log.debug("responseMap: {}", responseMap);
    zoomBookResponse.setResponseMap(responseMap);
    zoomBookResponse.setZoomBookResponseList(obj);
    return zoomBookResponse;
  }


}
