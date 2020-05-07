package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.oauth2.resource.service.SsoService;
import com.rivigo.riconet.core.constants.RedisTokenConstant;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.CollectionRequestDto;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.TransactionManagerService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.repository.redis.AccessTokenSsfRedisRepository;
import java.util.Collections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerServiceImpl implements TransactionManagerService {

  private final ApiClientService apiClientService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private SsoService ssoService;

  @Autowired private UserMasterService userMasterService;

  @Autowired
  @Qualifier("defaultRestClientUtilityServiceImpl")
  private RestClientUtilityService restClientUtilityService;

  @Value("${rivigo.sso.username}")
  private String ssoUsername;

  @Value("${rivigo.sso.password}")
  private String ssoPassword;

  @Value("${transaction.manager.url}")
  private String transactionManagerBaseUrl;

  @Autowired private AccessTokenSsfRedisRepository accessTokenSsfRedisRepository;

  @Override
  public void hitTransactionManagerAndLogResponse(
      @NonNull CollectionRequestDto collectionRequestDto) throws JsonProcessingException {

    User user = userMasterService.getByEmail(ssoPassword);

    String token = accessTokenSsfRedisRepository.get(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN);
    if (token == null) {
      log.info("No existing token found. New token is being generated ");
      token = ssoService.getUserAccessToken(ssoUsername, ssoPassword).getResponse();
      accessTokenSsfRedisRepository.set(RedisTokenConstant.RICONET_MASTER_LOGIN_TOKEN, token);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("token", token);
    headers.add("userId", user.getId().toString());
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestJson = objectMapper.writeValueAsString(collectionRequestDto);

    HttpEntity httpHeaders = new HttpEntity<>(requestJson, headers);

    log.debug("Hitting transaction manager with collectionRequestDto: {}", collectionRequestDto);

    restClientUtilityService.executeRest(
        transactionManagerBaseUrl + UrlConstant.TRANSACTION_MANAGER_URL,
        HttpMethod.POST,
        httpHeaders,
        Boolean.class);
  }
}
