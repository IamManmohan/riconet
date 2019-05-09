package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.datastore.DatastoreResponseDto;
import com.rivigo.riconet.core.enums.RequestStatus;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
@Slf4j
@Qualifier("datastoreRestClientUtilityServiceImpl")
public class DatastoreRestClientUtilityServiceImpl extends RestClientUtilityServiceImpl {

  @Override
  public <T> T executeRestApi(
      String url, HttpMethod httpMethod, HttpEntity entity, Class<T> clazz) {
    try {
      ResponseEntity<DatastoreResponseDto> responseEntity =
          restTemplate.exchange(url, httpMethod, entity, DatastoreResponseDto.class);
      if (responseEntity.getStatusCode().is5xxServerError()
          || responseEntity.getStatusCode().is4xxClientError()) {
        throw new ZoomException("Unable to connect to wms: {%s}", responseEntity.getStatusCode());
      }
      DatastoreResponseDto wmsResponse = responseEntity.getBody();
      if (wmsResponse == null) {
        throw new ZoomException("Unable to connect to wms");
      }
      if (wmsResponse.getStatus() == RequestStatus.FAILURE) {
        throw new ZoomException("Exception wms:" + wmsResponse.getErrorMessage());
      }
      return objectMapper.convertValue(wmsResponse.getPayload(), clazz);

    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }
}
