package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.wms.TaskDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.WMSService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WMSServiceImpl implements WMSService {

  @Autowired private ApiClientService apiClientService;

  @Value("${wms.url}")
  private String wmsBaseUrl;

  @Override
  public void createRTOForwardTask(String cnote, String userEmailId, String userLocationCode) {

    Map<String, List<String>> hmap = new HashMap<>();
    hmap.put("CNOTE", Collections.singletonList(cnote));

    TaskDTO taskDTO =
        TaskDTO.builder()
            .taskType("RTO_FORWARD")
            .locationCode(userLocationCode)
            .userEmail(userEmailId)
            .taskEntityMap(hmap)
            .build();

    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE;

    try {
      JsonNode responseJson =
          apiClientService.getEntity(taskDTO, HttpMethod.POST, url, null, wmsBaseUrl);
      log.debug(
          "Response from wms {} for taskDTO {} baseUrl {} endpoint {}",
          responseJson,
          taskDTO,
          wmsBaseUrl,
          url);

    } catch (IOException e) {
      log.error("Error while creating rto task {} , {}", taskDTO, e);
      throw new ZoomException("Error while creating rto task {}" + taskDTO);
    }
  }
}
