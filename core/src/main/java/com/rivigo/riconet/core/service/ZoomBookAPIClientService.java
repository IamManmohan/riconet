package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.dto.zoombook.TransactionModelDTO;
import com.rivigo.zoom.common.repository.mysql.ZoomBookTransactionRecordRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Service
@Slf4j
public class ZoomBookAPIClientService {

    private static final String FAILURE="FAILURE";
    private static final String STATUS="status";
    private static final String SUCCESS="SUCCESS";
    private static final String TIMEOUT="TIMEOUT";
    private static final String CLIENT_TOKEN="clientToken";
    private static final String CLIENT_KEY="clientKey";
    private static final String RESPONSE="response";
    private static final String STATUS_CODE="statusCode";

    @Value("${zoom.zoombook.url}")
    private String zoomBookUrl;

    @Value("${zoombookClientToken}")
    private String zoombookClientToken;

    @Value("${zoombookClientKey}")
    private String zoombookClientKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ZoomBookTransactionRecordRepository zoomBookTransactionRecordRepository;


    public List<TransactionModelDTO> getEntityCollectionsSummary(Long orgId, String functionType, String tenantType,
                                                                 Long fromDateTime, Long toDateTime, Boolean getAllByReference){
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
        TypeReference responseType=new TypeReference<List<TransactionModelDTO>>(){};
        Object response=getDataFromZoomBook(requestURL,queryParams,responseType);
        if(response==null){
            return new ArrayList<>();
        }
        return (List<TransactionModelDTO>)response;
    }

    public Object getDataFromZoomBook(String requestUrl, MultiValueMap<String, String> queryParams,TypeReference responseType){

        Map<String, String> zoomBookReponse = getDataFromZoomBook(requestUrl, queryParams);

        return getTransactionModelDetails(zoomBookReponse,responseType);
    }

    private Object getTransactionModelDetails(Map<String, String> zoomBookReponse,TypeReference responseType)  {
        if (null == zoomBookReponse || zoomBookReponse.get(STATUS).equals(FAILURE)) {
            throw new ZoomException("Failed to get Collections Summary From ZoomBook");
        }
        ObjectMapper mapper = new ObjectMapper();
        Object responseDto ;
        try {
            if(zoomBookReponse.get(RESPONSE)==null){
                return null;
            }
            responseDto = mapper.readValue(zoomBookReponse.get(RESPONSE), responseType);
        }catch (IOException e){
            log.error("Error while reading data from finance {} ",e);
            throw new ZoomException("Error while reading data from finance ");
        }
        return responseDto;
    }

    private Map<String, String> getDataFromZoomBook(String requestUrl, MultiValueMap<String, String> queryParams)  {
        Map<String, String> responseMap = new HashMap<>();
        try {
            log.info(" params {} ",queryParams);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add(CLIENT_KEY, zoombookClientKey);
            headers.add(CLIENT_TOKEN, zoombookClientToken);
            HttpEntity entity = new HttpEntity(headers);

            // Sample requestUrl : status
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(zoomBookUrl + "/" + requestUrl)
                    .queryParams(queryParams);

            log.info("Calling API for to GET data from Zoom-book");
            ResponseEntity<JsonNode> oauthResponse = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod
                    .GET, entity, JsonNode.class);
            JsonNode responseJson = oauthResponse.getBody();

            log.info("Completed API to get data from Zoom-book" );

            String statusCode = responseJson.get(STATUS_CODE).toString();
            String status = responseJson.get(STATUS).toString();

            if (oauthResponse.getStatusCode() == HttpStatus.OK) {
                responseMap.put(STATUS, SUCCESS);
                responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());
                if(200 == Integer.valueOf(statusCode)
                        && "\"SUCCESS\"".equals(status)){
                    responseMap.put(RESPONSE, responseJson.get(RESPONSE).toString());
                    return responseMap;
                }
                if(700 == Integer.valueOf(statusCode)){
                    responseMap.put(RESPONSE,null);
                    return responseMap;
                }
                if( 801 == Integer.valueOf(statusCode)){
                    responseMap.put(RESPONSE, "[]");
                    return responseMap;
                }
            }

            responseMap.put(STATUS, FAILURE);
            responseMap.put(STATUS_CODE, oauthResponse.getStatusCode().name());
            responseMap.put(RESPONSE, null);
            log.error("  {} ",responseJson.get(RESPONSE).toString());
            return responseMap;

        } catch (Exception e) {
            log.error("Unknown exception while trying to get data from zoom-book for url {} with params: {}", requestUrl, queryParams.toString());
            throw new ZoomException("Unknown exception while trying to get data from zoom-book");
        }
    }



}

