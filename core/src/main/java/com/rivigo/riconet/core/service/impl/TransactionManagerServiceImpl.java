package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.TransactionManagerService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerServiceImpl implements TransactionManagerService {

  private final ApiClientService apiClientService;

  @Value("${transaction.manager.url}")
  private String transactionManagerBaseUrl;

  @Override
  public void hitTransactionManagerAndLogResponse(
      @NonNull CollectionRequestDto collectionRequestDto) {

    try {
      JsonNode responseJson =
          apiClientService.getEntity(
              collectionRequestDto,
              HttpMethod.POST,
              UrlConstant.TRANSACTION_MANAGER_URL,
              null,
              transactionManagerBaseUrl);
      log.debug(
          "Response from transaction manager {} for collectionRequestDto {} baseUrl {} endpoint {}",
          responseJson,
          collectionRequestDto,
          transactionManagerBaseUrl,
          UrlConstant.TRANSACTION_MANAGER_URL);

    } catch (IOException e) {
      log.error("Error while processing collectionRequestDto {} , {}", collectionRequestDto, e);
      throw new ZoomException(
          "Error while processing collectionRequestDto {}" + collectionRequestDto);
    }
  }
}
