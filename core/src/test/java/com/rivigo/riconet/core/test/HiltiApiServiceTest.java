package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.impl.HiltiApiServiceImpl;
import com.rivigo.riconet.core.service.impl.RestClientUtilityServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.model.UndeliveredConsignment;
import com.rivigo.zoom.common.repository.mysql.ConsignmentUploadedFilesRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.mysql.UndeliveredConsignmentsRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Slf4j
public class HiltiApiServiceTest extends TesterBase {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RestClientUtilityServiceImpl restClientUtilityService;

  @Mock private PickupRepository pickupRepository;

  @Mock private LocationRepositoryV2 locationRepositoryV2;

  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Mock private ConsignmentScheduleService consignmentScheduleService;

  @Mock private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;

  @Mock private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;

  @InjectMocks private HiltiApiServiceImpl hiltiApiService;

  public static final List<String> CNOTES = Arrays.asList("6000320900", "6000339900", "6000345901", "6000472125");

  public static final int CNOTE_INDEX = 3;

  @Before
  public void initMocks() {
    RestClientUtilityService restClientUtilityServiceSpy = Mockito.spy(restClientUtilityService);
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(
        hiltiApiService,
        "hiltiUpdateTransactionsUrl",
        "https://staging.fareye.co/api/v1/update_transactions_status?api_key=VmyY0lEUNrj4eUUn5jqWYMgGjpeeLtDS");
    ReflectionTestUtils.setField(
        hiltiApiService, "restClientUtilityService", restClientUtilityServiceSpy);
    ReflectionTestUtils.setField(hiltiApiService, "objectMapper", objectMapper);
    Mockito.when(pickupRepository.findOne(ApiServiceUtils.PICKUP_ID))
        .thenReturn(ApiServiceUtils.getDummyPickup());
    Mockito.when(consignmentReadOnlyService.findConsignmentByPickupId(ApiServiceUtils.PICKUP_ID))
        .thenReturn(
            CNOTES
                .stream()
                .map(ApiServiceUtils::getDummyConsignmentWithCnote)
                .collect(Collectors.toList()));
    Mockito.when(consignmentReadOnlyService.findConsignmentById(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(
            Optional.of(ApiServiceUtils.getDummyConsignmentWithCnote(CNOTES.get(CNOTE_INDEX))));
    Mockito.when(consignmentScheduleService.getActivePlan(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(ApiServiceUtils.getDummyConsignmentSchedule());
    Mockito.when(locationRepositoryV2.findByIdIn(Mockito.anyList()))
        .thenReturn(ApiServiceUtils.getDummyLocations());
    Mockito.when(
            consignmentUploadedFilesRepository.findByConsignmentId(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(ApiServiceUtils.getDummyCnUploadedFiles());
    Mockito.when(
            undeliveredConsignmentsRepository
                .findTop1ByConsignmentIdAndOldDrsIdNotNullOrderByIdDesc(
                    ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(ApiServiceUtils.getDummyUndeliveredConsignment());
  }

  private void addPickupDoneEvent() {

    NotificationDTO notificationDTO = ApiServiceUtils.getDummyPickupCompleteNotificationDto();
    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addintransitArrivedEvent() {

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_RECEIVED_AT_OU, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addIntransitDispatchedEvent() {

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_LOADED, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addIntransitArrivedAtDestinationEvent() {
    Mockito.when(consignmentReadOnlyService.findConsignmentById(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(
            Optional.of(
                ApiServiceUtils.getDummyConsignmentWithCnoteAtDestination(
                    CNOTES.get(CNOTE_INDEX))));
    Mockito.when(consignmentScheduleService.getActivePlan(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(ApiServiceUtils.getDummyConsignmentScheduleAtDestination());

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_RECEIVED_AT_OU, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addDeliveryOFDEvent() {

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_OUT_FOR_DELIVERY, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addDeliveryDeliveredEvent() {

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_DELIVERY, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
  }

  private void addDeliveryUndeliveredEvent() {

    NotificationDTO notificationDTO =
        ApiServiceUtils.getDummyCnNotificationDtoForEvent(
            EventName.CN_UNDELIVERY, CNOTES.get(CNOTE_INDEX));

    List<HiltiRequestDto> requestDtos = hiltiApiService.getRequestDtosByType(notificationDTO);
    hiltiApiService.addEventsToQueue(requestDtos);
    hiltiApiService.publishEventsAndProcessErrors();
  }

  @Test
  public void allEventsE2ETest() throws InterruptedException{

    addPickupDoneEvent();
    addintransitArrivedEvent();
    addIntransitDispatchedEvent();
    sleep(1000);
    addIntransitArrivedAtDestinationEvent();
    addDeliveryOFDEvent();
    addDeliveryDeliveredEvent();
    sleep(100000);
  }
}
