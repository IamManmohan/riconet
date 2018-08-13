package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomBillingAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/** Created by chiragbansal on 13/06/18. */
@Slf4j
@Service
public class ZoomBillingAPIClientServiceImpl implements ZoomBillingAPIClientService {

  @Autowired private ApiClientService apiClientService;

  @Value("${billing.base.url}")
  private String billingBaseUrl;

  @Override
  public Double getChargedWeightForConsignment(String cnote) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BILLING_CN_DETAILS.replace("{cnote}", cnote);
    try {
      responseJson = apiClientService.getEntity(null, HttpMethod.GET, url, null, billingBaseUrl);
      return responseJson.get("response").get("chargedWeight").asDouble();
    } catch (Exception e) {
      log.error("Error while getting charged weight for cnote: {}", cnote, e);
      return 0.0;
    }
  }
}
