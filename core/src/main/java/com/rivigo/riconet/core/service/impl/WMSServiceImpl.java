package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.WMSService;
import com.rivigo.zoom.exceptions.ZoomException;
import com.rivigo.zoom.wms.client.dto.request.TaskCreateAssignDto;
import com.rivigo.zoom.wms.client.enums.TaskEntityType;
import com.rivigo.zoom.wms.client.enums.TaskType;
import com.rivigo.zoom.wms.client.enums.TaskUserType;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WMSServiceImpl implements WMSService {

  @Autowired private ApiClientService apiClientService;

  @Value("${zoom.wms.url}")
  private String wmsBaseUrl;

  @Override
  public void createOrReassignRTOForwardTask(
      @NonNull String cnote, @NonNull String userEmailId, @NonNull String userLocationCode) {

    Map<TaskEntityType, List<String>> hmap = new HashMap<>();
    hmap.put(TaskEntityType.CNOTE, Collections.singletonList(cnote));

    TaskCreateAssignDto taskCreateAssignDto =
        TaskCreateAssignDto.builder()
            .taskType(TaskType.RTO_FORWARD)
            .locationCode(userLocationCode)
            .taskUserEmailMap(
                Collections.singletonMap(TaskUserType.PA, Collections.singletonList(userEmailId)))
            .taskEntityMap(hmap)
            .build();

    String url = UrlConstant.WMS_SERVICE_TASK_CREATION;

    try {
      JsonNode responseJson =
          apiClientService.getEntity(taskCreateAssignDto, HttpMethod.POST, url, null, wmsBaseUrl);
      log.debug(
          "Response from wms {} for taskCreateAssignDto {} baseUrl {} endpoint {}",
          responseJson,
          taskCreateAssignDto,
          wmsBaseUrl,
          url);

    } catch (IOException e) {
      log.error("Error while creating rto task {} , {}", taskCreateAssignDto, e);
      throw new ZoomException("Error while creating rto task {}" + taskCreateAssignDto);
    }
  }
}
