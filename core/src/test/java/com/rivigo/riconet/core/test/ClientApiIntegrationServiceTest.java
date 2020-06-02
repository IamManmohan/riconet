package com.rivigo.riconet.core.test;

import static java.lang.Thread.sleep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.CnActionEventName;
import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.impl.ClientApiIntegrationServiceImpl;
import com.rivigo.riconet.core.service.impl.FlipkartClientIntegration;
import com.rivigo.riconet.core.service.impl.RestClientUtilityServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.repository.mysql.ConsignmentUploadedFilesRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.mysql.UndeliveredConsignmentsRepository;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
public class ClientApiIntegrationServiceTest {

  public static final List<String> CNOTES =
      Arrays.asList("6000320900", "6000339900", "6000345901", "6000472125");
  public static final int CNOTE_INDEX = 3;
  public static final List<String> clientIds =
      Arrays.asList(ClientConstants.HILTI_CLIENT_ID, ClientConstants.FLIPKART_SELLER_CLIENT);
  @Spy private ObjectMapper objectMapper;
  @Mock private RestClientUtilityServiceImpl restClientUtilityService;
  @Mock private PickupRepository pickupRepository;
  @Mock private LocationRepositoryV2 locationRepositoryV2;
  @Mock private ConsignmentReadOnlyService consignmentReadOnlyService;
  @Mock private ConsignmentScheduleService consignmentScheduleService;
  @Mock private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;
  @Mock private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;
  @Mock private ClientConsignmentService clientConsignmentService;
  @Mock private FlipkartClientIntegration flipkartClientIntegration;
  @InjectMocks private ClientApiIntegrationServiceImpl clientApiIntegrationService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(
        clientApiIntegrationService,
        "hiltiUpdateTransactionsUrl",
        "https://dummyHiltiUpdateTransactionUrl");
    ReflectionTestUtils.setField(
        clientApiIntegrationService,
        "flipkartUpdateTransactionUrl",
        "https://dummyFlipkartUpdateTransactionUrl");
    ReflectionTestUtils.setField(
        clientApiIntegrationService, "flipkartLoginUrl", "https://dummyFlipkartLoginUrl");

    ReflectionTestUtils.setField(
        clientApiIntegrationService, "flipkartClientId", "dummyFlipkartClientId");

    ReflectionTestUtils.setField(clientApiIntegrationService, "objectMapper", objectMapper);
    Mockito.when(pickupRepository.findById(ApiServiceUtils.PICKUP_ID))
        .thenReturn(ApiServiceUtils.getDummyPickup());
    Mockito.when(consignmentReadOnlyService.findByPickupId(ApiServiceUtils.PICKUP_ID))
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

    Mockito.when(clientConsignmentService.getCnoteToBarcodeMapFromCnoteList(Mockito.any()))
        .thenReturn(ApiServiceUtils.CNOTE_TO_BARCODE_MAP);

    Mockito.when(clientConsignmentService.getBarcodeListFromConsignmentId((Mockito.any())))
        .thenReturn(ApiServiceUtils.BARCODE_LIST);
    Mockito.when(
            clientConsignmentService.getCnoteToConsignmentMetadataMapFromCnoteList(Mockito.any()))
        .thenReturn(ApiServiceUtils.CNOTE_TO_METADATA_MAP);
    Mockito.when(restClientUtilityService.getHeaders()).thenReturn(new HttpHeaders());

    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.eq(clientApiIntegrationService.hiltiUpdateTransactionsUrl),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(Optional.of(ApiServiceUtils.getHiltiResponseDTO()));

    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.eq(clientApiIntegrationService.flipkartLoginUrl),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(Optional.of(ApiServiceUtils.getFlipkartLoginResponseDTO()));
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.eq(clientApiIntegrationService.flipkartUpdateTransactionUrl),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(Optional.of(ApiServiceUtils.getClientResponseDTO()));
    Mockito.when(flipkartClientIntegration.getFlipkartAccessToken())
        .thenReturn(ApiServiceUtils.getFlipkartLoginResponseDTO().getAccessToken());
  }

  private void addPickupDoneEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO = ApiServiceUtils.getDummyPickupCompleteNotificationDto();
      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addintransitArrivedEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_RECEIVED_AT_OU, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addIntransitDispatchedEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_LOADED, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addIntransitArrivedAtDestinationEvent() {
    Mockito.when(consignmentReadOnlyService.findConsignmentById(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(
            Optional.of(
                ApiServiceUtils.getDummyConsignmentWithCnoteAtDestination(
                    CNOTES.get(CNOTE_INDEX))));
    Mockito.when(consignmentScheduleService.getActivePlan(ApiServiceUtils.CONSIGNMENT_ID))
        .thenReturn(ApiServiceUtils.getDummyConsignmentScheduleAtDestination());

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_RECEIVED_AT_OU, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addDeliveryOFDEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_OUT_FOR_DELIVERY, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addDeliveryDeliveredEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_DELIVERY, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  private void addDeliveryUndeliveredEvent() {

    for (String client : clientIds) {
      NotificationDTO notificationDTO =
          ApiServiceUtils.getDummyCnNotificationDtoForEvent(
              CnActionEventName.CN_UNDELIVERY, CNOTES.get(CNOTE_INDEX));

      clientApiIntegrationService.getClientRequestDtosByType(notificationDTO, client);
    }
  }

  @Test
  public void allEventsE2ETest() throws InterruptedException {

    addPickupDoneEvent();
    addintransitArrivedEvent();
    addIntransitDispatchedEvent();
    sleep(1000);
    addIntransitArrivedAtDestinationEvent();
    addDeliveryOFDEvent();
    addDeliveryDeliveredEvent();
    addDeliveryUndeliveredEvent();
    clientApiIntegrationService.publishEventsOfHiltiAndProcessErrors();
    clientApiIntegrationService.publishEventsOfFlipkartAndProcessErrors();
  }
}
