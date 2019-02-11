package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.QcResponseDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.QcApiClientService;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by ashfakh on 11/02/19.
 */

@Slf4j
@Service
public class QcApiClientServiceImpl implements QcApiClientService {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${zoom.qc.url}")
    private String qcBaseUrl;

    @Autowired private ApiClientService apiClientService;

    @Override
    public QcResponseDTO getQcFlag(Long cnId) {
        JsonNode responseJson;
        MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
        valuesMap.put("consignmentId", Collections.singletonList(cnId.toString()));
        try {
            responseJson =
                    apiClientService.getEntity(null, HttpMethod.POST, null, valuesMap, qcBaseUrl);
        } catch (IOException e) {
            log.error("Error while calling QC API for cn id {}", cnId, e);
            throw new ZoomException(
                    "Error while calling QC API for cn id : " + cnId);
        }
        TypeReference<TicketDTO> mapType = new TypeReference<TicketDTO>() {};

        return (QcResponseDTO) apiClientService.parseJsonNode(responseJson, mapType);
    }

}
