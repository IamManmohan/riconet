package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.client.LogiFreightRestServiceRequest;
import com.rivigo.riconet.core.constants.LogiFreightConstants;
import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryRequestDto;
import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryResponseDto;
import com.rivigo.riconet.core.dto.logifreight.ReleaseLrHoldRequestDto;
import com.rivigo.riconet.core.dto.logifreight.ReleaseLrHoldResponseDto;
import com.rivigo.riconet.core.dto.logifreight.UploadPodResponseDto;
import com.rivigo.riconet.core.dto.logifreight.UserLoginRequestDto;
import com.rivigo.riconet.core.dto.logifreight.UserLoginResponseDto;
import com.rivigo.riconet.core.exception.CustomException;
import com.rivigo.riconet.core.service.CacheService;
import com.rivigo.riconet.core.service.LogiFreightRestService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogiFreightRestServiceImpl implements LogiFreightRestService {

  @Value("${external.logifreight.base.url}")
  private String logiFreightUrl;

  @Value("${external.logifreight.timeout.millis}")
  private Long timeoutMillis;

  @Value("${external.logifreight.retry.attempts}")
  private Integer retryAttemptCount;

  @Value("${external.logifreight.shipper.code}")
  private String airCnShipperCompanyCode;

  @Value("${logifreight.user.login.release.email}")
  private String releaseLoginEmail;

  @Value("${logifreight.user.login.release.password}")
  private String releaseLoginPassword;

  @Value("${logifreight.user.login.delivery.email}")
  private String deliveryAndPodLoginEmail;

  @Value("${logifreight.user.login.delivery.password}")
  private String deliveryAndPodLoginPassword;

  @Autowired
  @Qualifier("defaultRestClientUtilityServiceImpl")
  private RestClientUtilityService restClientUtilityService;

  private final CacheService cacheService;

  private HttpHeaders getLogiFreightHeadersWithToken(
      String email, String password, String cacheKey) {
    HttpHeaders headers = restClientUtilityService.getHeaders();
    headers.set(
        LogiFreightConstants.LOGIFREIGHT_API_KEY_HEADER_NAME,
        getUserLoginToken(email, password, cacheKey));
    return headers;
  }

  @Override
  public String getUserLoginToken(String email, String password, String cacheKey) {
    String token = cacheService.get(cacheKey);
    if (null != token) {
      return token;
    }
    return generateUserLoginToken(email, password, cacheKey);
  }

  /**
   * Optional<T> executeRest( String baseUrl, RetryRestRequest retryRestRequest, Map<String, String>
   * pathVariableValueMap, MultiValueMap<String, String> paramMap, Object body,
   * MultiValueMap<String, String> customHeaders, Long timeOut, Integer retryAttempts
   *
   * @param email
   * @param password
   * @param cacheKay
   * @return
   */
  private String generateUserLoginToken(String email, String password, String cacheKay) {
    UserLoginRequestDto userLoginRequestDto =
        new UserLoginRequestDto(new UserLoginRequestDto.UserDto(email, password));
    try {
      UserLoginResponseDto userLoginResponseDto =
          restClientUtilityService
              .executeRest(
                  logiFreightUrl,
                  LogiFreightRestServiceRequest.GET_USER_LOGIN_DETAILS,
                  null,
                  null,
                  userLoginRequestDto,
                  restClientUtilityService.getHeaders(),
                  UserLoginResponseDto.class,
                  timeoutMillis,
                  LogiFreightRestServiceRequest.GET_USER_LOGIN_DETAILS.isRetryEndpoint()
                      ? retryAttemptCount
                      : 0)
              .orElseThrow(
                  () -> new CustomException("Failed to fetch user login from logifreight."));
      if (null == userLoginRequestDto.getUser()) {
        throw new CustomException("Failed to fetch user from logifreight login dto.");
      }

      String apiKey = userLoginResponseDto.getUser().getApiKey();
      cacheService.setValueWithTtl(
          cacheKay, apiKey, LogiFreightConstants.USER_LOGIN_KEY_CACHE_DURATION_IN_MILLIS);
      return apiKey;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new CustomException("Failed to fetch user login from logifreight.");
    }
  }

  // Release LR hold before recording delivery
  // No retries for release api as it is not giving response in usual json format
  @Override
  public ReleaseLrHoldResponseDto releaseLrHold(String lrNumber) {
    log.info("Releasing LR before marking delivery");
    ReleaseLrHoldRequestDto.ConsignmentHoldRequestDto cnHoldRequestDto =
        ReleaseLrHoldRequestDto.ConsignmentHoldRequestDto.builder()
            .consignment_number(lrNumber)
            .shipper_company_code(airCnShipperCompanyCode)
            .build();

    ReleaseLrHoldRequestDto requestDto =
        ReleaseLrHoldRequestDto.builder().consignment(cnHoldRequestDto).build();
    try {
      ReleaseLrHoldResponseDto releaseLrHoldResponseDto =
          restClientUtilityService
              .executeRest(
                  logiFreightUrl,
                  LogiFreightRestServiceRequest.RELEASE_HOLD,
                  null,
                  null,
                  cnHoldRequestDto,
                  getLogiFreightHeadersWithToken(
                      releaseLoginEmail,
                      releaseLoginPassword,
                      LogiFreightConstants.RELEASE_USER_LOGIN_TOKEN_KEY),
                  ReleaseLrHoldResponseDto.class,
                  timeoutMillis,
                  LogiFreightRestServiceRequest.RELEASE_HOLD.isRetryEndpoint()
                      ? retryAttemptCount
                      : 0)
              .orElseThrow(() -> new CustomException("Failed to release hold from logifreight."));
      log.info("releaseLrHoldResponseDto: {}", releaseLrHoldResponseDto);
      return releaseLrHoldResponseDto;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  // record delivery
  @Override
  public RecordDeliveryResponseDto recordConsignmentDelivery(RecordDeliveryRequestDto requestDto) {
    log.info("marking consignment delivered in logiFreight: {}", requestDto);
    try {
      RecordDeliveryResponseDto recordDeliveryResponseDto =
          restClientUtilityService
              .executeRest(
                  logiFreightUrl,
                  LogiFreightRestServiceRequest.RECORD_CONSIGNMENT_DELIVERY,
                  null,
                  null,
                  requestDto,
                  getLogiFreightHeadersWithToken(
                      deliveryAndPodLoginEmail,
                      deliveryAndPodLoginPassword,
                      LogiFreightConstants.DELIVERY_USER_LOGIN_TOKEN_KEY),
                  RecordDeliveryResponseDto.class,
                  timeoutMillis,
                  LogiFreightRestServiceRequest.RECORD_CONSIGNMENT_DELIVERY.isRetryEndpoint()
                      ? retryAttemptCount
                      : 0)
              .orElseThrow(() -> new CustomException("Failed to record delivery to logifreight."));
      log.info("recordDeliveryResponseDto: {}", recordDeliveryResponseDto);
      return recordDeliveryResponseDto;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new CustomException(e.getMessage());
    }
  }

  // Upload pod
  @Override
  public UploadPodResponseDto uploadPod(String lrNumber, String path) {
    log.info("uploading pod for LR:{}", lrNumber);
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(
        LogiFreightConstants.CONSIGNMENT_NUMBER_REQUEST_PARAM, Collections.singletonList(lrNumber));
    params.put(
        LogiFreightConstants.ATTACHMENT_TYPE_REQUEST_PARAM,
        Collections.singletonList(LogiFreightConstants.UPLOAD_POD_TYPE));
    params.put(LogiFreightConstants.FILEPATH_REQUEST_PARAM, Collections.singletonList(path));
    params.put(
        LogiFreightConstants.IS_VERIFIED_REQUEST_PARAM,
        Collections.singletonList(LogiFreightConstants.IS_VERIFIED_VALUE));
    try {
      return restClientUtilityService
          .executeRest(
              logiFreightUrl,
              LogiFreightRestServiceRequest.UPLOAD_POD,
              null,
              params,
              null,
              getLogiFreightHeadersWithToken(
                  releaseLoginEmail,
                  releaseLoginPassword,
                  LogiFreightConstants.DELIVERY_USER_LOGIN_TOKEN_KEY),
              UploadPodResponseDto.class,
              timeoutMillis,
              LogiFreightRestServiceRequest.UPLOAD_POD.isRetryEndpoint() ? retryAttemptCount : 0)
          .orElseThrow(() -> new CustomException("Failed to upload POD to Logifreight."));
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }
}
