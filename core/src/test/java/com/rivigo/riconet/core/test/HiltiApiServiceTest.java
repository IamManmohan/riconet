package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.impl.HiltiApiServiceImpl;
import com.rivigo.riconet.core.service.impl.RestClientUtilityServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class HiltiApiServiceTest extends TesterBase {

  @Autowired private ObjectMapper objectMapper;

  @Autowired
  private RestClientUtilityServiceImpl restClientUtilityService;

  @Mock private PickupRepository pickupRepository;

  @Mock private LocationRepositoryV2 locationRepositoryV2;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @InjectMocks private HiltiApiServiceImpl hiltiApiService;

  @Before
  public void initMocks() {
    RestClientUtilityService restClientUtilityServiceSpy = Mockito.spy(restClientUtilityService);
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(hiltiApiService, "hiltiUpdateTransactionsUrl", "https://staging.fareye.co/api/v1/update_transactions_status?api_key=VmyY0lEUNrj4eUUn5jqWYMgGjpeeLtDS");
    ReflectionTestUtils.setField(hiltiApiService, "restClientUtilityService", restClientUtilityServiceSpy);
    ReflectionTestUtils.setField(hiltiApiService, "objectMapper", objectMapper);
  }

  @Test
  public void pickupCompletionTest() {

    NotificationDTO notificationDTO = ApiServiceUtils.getDummyPickupCompleteNotificationDto();

    Mockito.when(pickupRepository.findOne(ApiServiceUtils.PICKUP_ID)).thenReturn(ApiServiceUtils.getDummyPickup());
    Mockito.when(consignmentReadOnlyService.findConsignmentByPickupId(ApiServiceUtils.PICKUP_ID)).thenReturn(Arrays.asList(ApiServiceUtils.getConsignmentWithCnote("6000089780")));
    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
    hiltiApiService.publishEventsAndProcessErrors();
  }


}
