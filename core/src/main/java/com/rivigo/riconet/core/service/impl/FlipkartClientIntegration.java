package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.FlipkartUtilConstants;
import com.rivigo.riconet.core.dto.client.FlipkartLoginResponseDTO;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author jaiprakash
 * @version 1.0
 */
@Component
@Slf4j
public class FlipkartClientIntegration {

  private static Long EXPIRY_BUFFER = 60 * 1000l;

  private Long validityOfAccessToken = System.currentTimeMillis();

  private String accessToken = null;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Value("${flipkart.login.password}")
  private String flipkartLoginPassword;

  @Value("${flipkart.client.id}")
  private String flipkartClientId;

  @Value("${flipkart.tenant.id}")
  private String flipkartTenantId;

  @Value("${flipkart.login.url}")
  public String flipkartLoginUrl;

  @Autowired
  @Qualifier("defaultRestClientUtilityServiceImpl")
  private RestClientUtilityService restClientUtilityService;

  public String getFlipkartAccessToken() {
    /** Calling Flipkart Login Api */
    log.info("Sending login request to flipkart");
    if (validityOfAccessToken < System.currentTimeMillis()) {
      synchronized (FlipkartClientIntegration.class) {
        if (validityOfAccessToken < System.currentTimeMillis()) {
          FlipkartLoginResponseDTO loginResponseDto =
              objectMapper.convertValue(
                  loginToFlipkart()
                      .orElseThrow(() -> new ZoomException("Unable to login to Flipkart")),
                  FlipkartLoginResponseDTO.class);
          log.info("Login Response from Flipkart: {}", loginResponseDto);
          if (loginResponseDto.getError() != null) {
            throw new ZoomException(
                "Unable to login to Flipkart as error received {} ",
                loginResponseDto.getErrorDescription());
          }
          accessToken = loginResponseDto.getAccessToken();
          validityOfAccessToken =
              System.currentTimeMillis() + (loginResponseDto.getExpiresIn() * 1000) - EXPIRY_BUFFER;
        }
      }
    }
    return accessToken;
  }

  private Optional<?> loginToFlipkart() {
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.put(
        FlipkartUtilConstants.FLIPKART_GRANT_TYPE_PARAM,
        Collections.singletonList(FlipkartUtilConstants.FLIPKART_GRANT_TYPE_PARAM_VALUE));
    parameters.put(
        FlipkartUtilConstants.FLIPKART_CLIENT_ID, Collections.singletonList(flipkartClientId));
    parameters.put(
        FlipkartUtilConstants.FLIPKART_CLIENT_SECRET,
        Collections.singletonList(flipkartLoginPassword));
    parameters.put(
        FlipkartUtilConstants.FLIPKART_TARGET_CLIENT_ID,
        Collections.singletonList(flipkartTenantId));
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<?> entity = new HttpEntity<>(parameters, headers);
    return restClientUtilityService.executeRest(
        flipkartLoginUrl, HttpMethod.POST, entity, FlipkartLoginResponseDTO.class);
  }
}
