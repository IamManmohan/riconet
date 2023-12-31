package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.RestUtilConstants;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.client.ClientIntegrationResponseDTO;
import com.rivigo.riconet.core.dto.client.FlipkartRequestDTO;
import com.rivigo.riconet.core.dto.hilti.BaseHiltiFieldData;
import com.rivigo.riconet.core.dto.hilti.DeliveryDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryNotDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryOFDDto;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.riconet.core.dto.hilti.HiltiResponseDto;
import com.rivigo.riconet.core.dto.hilti.IntransitArrivedDto;
import com.rivigo.riconet.core.dto.hilti.IntransitDispatchedDto;
import com.rivigo.riconet.core.dto.hilti.PickupDoneDto;
import com.rivigo.riconet.core.enums.CnActionEventName;
import com.rivigo.riconet.core.enums.HiltiJobType;
import com.rivigo.riconet.core.enums.HiltiStatusCode;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.ClientApiIntegrationService;
import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.ConsignmentUploadedFiles;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.UndeliveredConsignment;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentUploadedFilesRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.mysql.UndeliveredConsignmentsRepository;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class ClientApiIntegrationServiceImpl implements ClientApiIntegrationService {

  @Value("${hilti.update.transactions.url}")
  public String hiltiUpdateTransactionsUrl;

  @Value("${flipkart.login.url}")
  public String flipkartLoginUrl;

  @Value("${flipkart.update.transaction.url}")
  public String flipkartUpdateTransactionUrl;

  @Value("${flipkart.client.id}")
  private String flipkartClientId;

  @Autowired
  @Qualifier("defaultRestClientUtilityServiceImpl")
  private RestClientUtilityService restClientUtilityService;

  @Autowired private PickupRepository pickupRepository;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private FlipkartClientIntegration flipkartClientIntegration;

  @Autowired private LocationRepositoryV2 locationRepositoryV2;

  @Autowired private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Autowired private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;

  @Autowired private ClientConsignmentService clientConsignmentService;

  @Autowired private AdministrativeEntityService administrativeEntityService;

  private ObjectMapper objectMapper = new ObjectMapper();

  private BlockingQueue<HiltiRequestDto> eventBuffer = new LinkedBlockingQueue<>();

  private BlockingQueue<FlipkartRequestDTO> clientEventBuffer = new LinkedBlockingQueue<>();

  private List<HiltiRequestDto> hiltiRequestDtoList = new ArrayList<>();

  private List<FlipkartRequestDTO> clientIntegrationRequestDtoList = new ArrayList<>();

  private Optional<?> sendRequestToHilti(List<HiltiRequestDto> requestDtos) {
    return restClientUtilityService.executeRest(
        hiltiUpdateTransactionsUrl,
        HttpMethod.POST,
        new HttpEntity<>(requestDtos, restClientUtilityService.getHeaders()),
        Object.class);
  }

  private Optional<?> sendRequestToFlipkart(List<FlipkartRequestDTO> requestDtos) {
    String accessToken = flipkartClientIntegration.getFlipkartAccessToken();
    HttpHeaders headers = restClientUtilityService.getHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    headers.set(RestUtilConstants.X_CLIENT_ID, flipkartClientId);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    return restClientUtilityService.executeRest(
        flipkartUpdateTransactionUrl,
        HttpMethod.POST,
        new HttpEntity<>(requestDtos, headers),
        Object.class);
  }

  private List<HiltiRequestDto> getPickupRequestDtos(
      NotificationDTO notificationDTO, Boolean addBarcodes) {
    Pickup pickup = pickupRepository.findOne(notificationDTO.getEntityId());
    List<ConsignmentReadOnly> cnList =
        consignmentReadOnlyService.findByPickupId(notificationDTO.getEntityId());
    Map<String, List<String>> cnoteToBarcodesMap =
        clientConsignmentService.getCnoteToBarcodeMapFromCnoteList(
            cnList.stream().map(ConsignmentReadOnly::getCnote).collect(Collectors.toList()));
    final List<Long> consignmentIds =
        cnList.stream().map(ConsignmentReadOnly::getId).collect(Collectors.toList());

    // fetching the consignment schedules for all consignments.
    final Map<Long, List<ConsignmentSchedule>> consignmentScheduleListForAllCNs =
        consignmentScheduleService.getActivePlansMapByIds(consignmentIds);

    Map<Long, ConsignmentHistory> lastScanByCnIdIn =
        consignmentService.getLastScanByCnIdIn(
            consignmentIds, Collections.singletonList(ConsignmentStatus.INTRANSIT_TO_OU.name()));

    return cnList
        .stream()
        .map(
            v -> {
              PickupDoneDto pickupDoneDto =
                  PickupDoneDto.builder()
                      .pickupTime(
                          TimeUtilsZoom.getTime(
                              Optional.ofNullable(lastScanByCnIdIn.get(v.getId()))
                                  .map(ConsignmentHistory::getCreatedAt)
                                  .orElse(v.getCreatedAt())))
                      .expectedDeliveryDate(TimeUtilsZoom.getDate(v.getPromisedDeliveryDateTime()))
                      .build();
              if (addBarcodes) {
                pickupDoneDto.setBarcodes(cnoteToBarcodesMap.get(v.getCnote()));
              }
              pickupDoneDto.setDate(TimeUtilsZoom.getDate(pickup.getPickupDate()));

              if (consignmentScheduleListForAllCNs.containsKey(v.getId())) {
                final List<ConsignmentSchedule> consignmentSchedules =
                    consignmentScheduleListForAllCNs.get(v.getId());
                pickupDoneDto.setRevisedEdd(
                    getRevisedEddFromCnScheduleAndConsignment(
                        consignmentSchedules, v.getPromisedDeliveryDateTime()));
              }

              return HiltiRequestDto.builder()
                  .jobType(HiltiJobType.PICKUP.toString())
                  .newStatusCode(HiltiStatusCode.PICKUP_DONE.toString())
                  .referenceNumber(v.getCnote())
                  .fieldData(pickupDoneDto)
                  .build();
            })
        .collect(Collectors.toList());
  }

  private BaseHiltiFieldData getIntransitFieldData(NotificationDTO notificationDTO) {
    log.info("Getting consignment for id {}", notificationDTO.getEntityId());
    ConsignmentReadOnly consignment =
        consignmentReadOnlyService
            .findConsignmentById(notificationDTO.getEntityId())
            .orElseThrow(
                () -> new ZoomException("Unable to get consignment from " + notificationDTO));

    List<ConsignmentSchedule> schedules =
        consignmentScheduleService.getActivePlan(notificationDTO.getEntityId());

    Optional<ConsignmentSchedule> nextSchedule =
        schedules
            .stream()
            .filter(v -> v.getLocationType() == LocationTypeV2.LOCATION)
            .filter(v -> v.getPlanStatus() == ConsignmentLocationStatus.NOT_REACHED)
            .findFirst();
    List<Location> locations =
        locationRepositoryV2.findByIdIn(
            nextSchedule
                .map(v -> Arrays.asList(consignment.getLocationId(), v.getLocationId()))
                .orElse(Collections.singletonList(consignment.getLocationId())));
    Map<Long, String> idToLocationNameMap =
        locations.stream().collect(Collectors.toMap(Location::getId, Location::getName));
    switch (CnActionEventName.valueOf(notificationDTO.getEventName())) {
      case CN_RECEIVED_AT_OU:
        AdministrativeEntity currentCluster =
            administrativeEntityService.findParentCluster(consignment.getLocationId());
        AdministrativeEntity deliveryCluster =
            administrativeEntityService.findParentCluster(consignment.getToLocationId());
        boolean atDestination = currentCluster.getId().equals(deliveryCluster.getId());
        return IntransitArrivedDto.builder()
            .arrivedAt(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .atDestination(atDestination ? "yes" : "no")
            .revisedEdd(
                getRevisedEddFromCnScheduleAndConsignment(
                    schedules, consignment.getPromisedDeliveryDateTime()))
            .build();
      case CN_TRIP_DISPATCHED:
        Long departureTime =
            Optional.ofNullable(
                    notificationDTO
                        .getMetadata()
                        .get(ZoomCommunicationFieldNames.ConsignmentSchedule.DEPARTURE_TIME.name()))
                .map(Long::valueOf)
                .orElse(notificationDTO.getTsMs());
        return IntransitDispatchedDto.builder()
            .dispatchedFrom(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .dispatchedTo(
                idToLocationNameMap.getOrDefault(
                    nextSchedule.map(ConsignmentSchedule::getLocationId).orElse(null), ""))
            .revisedEdd(
                getRevisedEddFromCnScheduleAndConsignment(
                    schedules, consignment.getPromisedDeliveryDateTime()))
            .date(TimeUtilsZoom.getDate(new DateTime(departureTime)))
            .time(TimeUtilsZoom.getTime(new DateTime(departureTime)))
            .build();
      default:
        log.error("Unrecognized intransit event {}", notificationDTO);
        throw new ZoomException("Unrecognized intransit event " + notificationDTO);
    }
  }

  /**
   * This function finds the revised edd(scheduled arrival time) from cn schedule list where
   * schedule locationType = PINCODE and locationTag = TO_PINCODE.
   *
   * @param schedules consignment schedule collection.
   * @param cnPromisedDeliveryDateTime promised delivery date time from consignment table.
   * @return revised edd in string format.
   */
  private static String getRevisedEddFromCnScheduleAndConsignment(
      @NonNull Collection<ConsignmentSchedule> schedules,
      @NonNull DateTime cnPromisedDeliveryDateTime) {
    final Optional<ConsignmentSchedule> toPincodeCnScheduleOptional =
        schedules
            .stream()
            .filter(
                s ->
                    LocationTypeV2.PINCODE == s.getLocationType()
                        && LocationTag.TO_PINCODE == s.getLocationTag())
            .findAny();

    if (toPincodeCnScheduleOptional.isPresent()) {
      return TimeUtilsZoom.getDate(
          new DateTime(
              Math.max(
                  toPincodeCnScheduleOptional.get().getArrivalScheduledTime(),
                  cnPromisedDeliveryDateTime.getMillis())));
    }
    return TimeUtilsZoom.getDate(cnPromisedDeliveryDateTime);
  }

  private BaseHiltiFieldData getDeliveryFieldData(NotificationDTO notificationDTO) {
    List<ConsignmentSchedule> consignmentSchedules;
    ConsignmentReadOnly consignmentReadOnly;
    switch (CnActionEventName.valueOf(notificationDTO.getEventName())) {
      case CN_OUT_FOR_DELIVERY:
        return DeliveryOFDDto.builder().build();
      case CN_DELIVERY:
        List<ConsignmentUploadedFiles> uploadedDocuments =
            consignmentUploadedFilesRepository.findByConsignmentId(notificationDTO.getEntityId());
        Map<FileTypes, String> fileTypeToUrlMap =
            uploadedDocuments
                .stream()
                .collect(
                    Collectors.toMap(
                        ConsignmentUploadedFiles::getFileTypes,
                        ConsignmentUploadedFiles::getS3URL));

        return DeliveryDeliveredDto.builder()
            .podDelivered(fileTypeToUrlMap.getOrDefault(FileTypes.POD, ""))
            .codImage(fileTypeToUrlMap.getOrDefault(FileTypes.COD_DOD, ""))
            .deliverySignature(fileTypeToUrlMap.getOrDefault(FileTypes.DELIVERY_CHALLAN, ""))
            .build();
      case CN_UNDELIVERY:
        UndeliveredConsignment undeliveredConsignment =
            undeliveredConsignmentsRepository
                .findTop1ByConsignmentIdAndOldDrsIdNotNullOrderByIdDesc(
                    notificationDTO.getEntityId());
        consignmentReadOnly =
            consignmentReadOnlyService.findRequiredById(notificationDTO.getEntityId());
        consignmentSchedules =
            consignmentScheduleService.getActivePlan(notificationDTO.getEntityId());
        return DeliveryNotDeliveredDto.builder()
            .undeliveryReason(
                undeliveredConsignment.getReason() + ": " + undeliveredConsignment.getSubReason())
            .podUndelivered("")
            .rdd(
                getRevisedEddFromCnScheduleAndConsignment(
                    consignmentSchedules, consignmentReadOnly.getPromisedDeliveryDateTime()))
            .build();
      default:
        log.error("Unrecognized delivery event {}", notificationDTO);
        throw new ZoomException("Unrecognized delivery event " + notificationDTO);
    }
  }

  private BaseHiltiFieldData getFieldDataForCnEvents(
      NotificationDTO notificationDTO, Boolean addBarcodes) {
    BaseHiltiFieldData fieldData;
    switch (CnActionEventName.valueOf(notificationDTO.getEventName())) {
      case CN_RECEIVED_AT_OU:
      case CN_TRIP_DISPATCHED:
        fieldData = getIntransitFieldData(notificationDTO);
        break;
      case CN_OUT_FOR_DELIVERY:
      case CN_DELIVERY:
      case CN_UNDELIVERY:
        fieldData = getDeliveryFieldData(notificationDTO);
        break;
      default:
        log.error("Unrecognized event {}", notificationDTO);
        throw new ZoomException("Unrecognized event {}" + notificationDTO);
    }
    if (!CnActionEventName.CN_TRIP_DISPATCHED.name().equals(notificationDTO.getEventName())) {
      fieldData.setTime(TimeUtilsZoom.getTime(new DateTime(notificationDTO.getTsMs())));
      fieldData.setDate(TimeUtilsZoom.getDate(new DateTime(notificationDTO.getTsMs())));
    }
    if (addBarcodes) {
      List<String> barCodes =
          clientConsignmentService.getBarcodeListFromConsignmentId(notificationDTO.getEntityId());
      fieldData.setBarcodes(barCodes);
    }
    log.info("fieldData barcodes {}", fieldData.getBarcodes());
    return fieldData;
  }

  private List<HiltiRequestDto> getHiltiRequestDtosByType(
      NotificationDTO notificationDTO, Boolean addBarcodes) {

    HiltiJobType jobType;
    HiltiStatusCode statusCode;

    switch (CnActionEventName.valueOf(notificationDTO.getEventName())) {
      case PICKUP_COMPLETION:
        return getPickupRequestDtos(notificationDTO, addBarcodes);
      case CN_RECEIVED_AT_OU:
        jobType = HiltiJobType.INTRANSIT;
        statusCode = HiltiStatusCode.ARRIVED;
        break;
      case CN_TRIP_DISPATCHED:
        jobType = HiltiJobType.INTRANSIT;
        statusCode = HiltiStatusCode.DISPATCHED;
        break;
      case CN_OUT_FOR_DELIVERY:
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.OUT_FOR_DELIVERY;
        break;
      case CN_DELIVERY:
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.DELIVERED;
        break;
      case CN_UNDELIVERY:
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.NOT_DELIVERED;
        break;
      default:
        log.error("Invalid event captured. Unable to process {}", notificationDTO);
        throw new ZoomException("Invalid event captured. Unable to process " + notificationDTO);
    }
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.toString());
    if (Strings.isNullOrEmpty(cnote)) {
      cnote =
          consignmentReadOnlyService
              .findConsignmentById(notificationDTO.getEntityId())
              .map(ConsignmentReadOnly::getCnote)
              .orElseThrow(
                  () -> new ZoomException("Unable to get consignment from " + notificationDTO));
    }

    return Collections.singletonList(
        HiltiRequestDto.builder()
            .jobType(jobType.toString())
            .newStatusCode(statusCode.toString())
            .referenceNumber(cnote)
            .fieldData(getFieldDataForCnEvents(notificationDTO, addBarcodes))
            .build());
  }

  public void getClientRequestDtosByType(NotificationDTO notificationDTO, String clientId) {
    switch (clientId) {
      case ClientConstants.HILTI_CLIENT_ID:
      case ClientConstants.HILTI_CLIENT_ID_DEP:
      case ClientConstants.HILTI_CLIENT_ID_CD:
        List<HiltiRequestDto> hiltiDtoList = getHiltiRequestDtosByType(notificationDTO, false);
        addEventsToQueue(hiltiDtoList, eventBuffer);
        break;
      case ClientConstants.FLIPKART_SELLER_CLIENT:
      case ClientConstants.FLIPKART_INDIA_CLIENT:
      case ClientConstants.FLIPKART_INDIA_ZOOM_CLIENT:
      case ClientConstants.FLIPKART_INTERNET_SELLER_CLIENT:
        List<HiltiRequestDto> hiltiRequestDtoListForFlipkart =
            getHiltiRequestDtosByType(notificationDTO, true);
        List<String> cnoteList =
            hiltiRequestDtoListForFlipkart
                .stream()
                .map(HiltiRequestDto::getReferenceNumber)
                .collect(Collectors.toList());
        Map<String, Map<String, String>> cnoteToConsignmentMetadataMap =
            clientConsignmentService.getCnoteToConsignmentMetadataMapFromCnoteList(cnoteList);

        List<FlipkartRequestDTO> clientIntegrationRequestDTOList = new ArrayList<>();
        FlipkartRequestDTO clientIntegrationRequestDto;
        for (HiltiRequestDto hiltiRequestDto : hiltiRequestDtoListForFlipkart) {
          clientIntegrationRequestDto = new FlipkartRequestDTO(hiltiRequestDto);
          clientIntegrationRequestDto.setMetaData(
              Optional.ofNullable(
                      cnoteToConsignmentMetadataMap.get(hiltiRequestDto.getReferenceNumber()))
                  .orElse(Collections.emptyMap()));
          clientIntegrationRequestDTOList.add(clientIntegrationRequestDto);
        }
        log.info("List of flipkart dtos: {}", clientIntegrationRequestDTOList.toString());
        addEventsToQueue(clientIntegrationRequestDTOList, clientEventBuffer);
        break;
      case ClientConstants.LOGI_FREIGHT_CLIENT_ID:
      case ClientConstants.PFIZER_CLIENT_ID:
      case ClientConstants.PFIZER_CLIENT_ID_IND:
        // validate and mark the consignments delivered in shipX
        String cnote = consignmentService.getCnoteByIdAndIsActive(notificationDTO.getEntityId());
        boolean isPrimaryCn = consignmentService.isPrimaryConsignment(cnote);
        if (CnActionEventName.CN_DELIVERY.name().equals(notificationDTO.getEventName())
            && isPrimaryCn) {
          log.info("NotificationDTO for LOGIFREIGHT:{}", notificationDTO);
          List<ConsignmentUploadedFiles> uploadedFiles =
              consignmentUploadedFilesRepository.findByFileTypesAndConsignmentId(
                  FileTypes.POD, notificationDTO.getEntityId());
          ConsignmentUploadedFiles consignmentUploadedFiles =
              CollectionUtils.isEmpty(uploadedFiles) ? null : uploadedFiles.get(0);
          clientConsignmentService.validateLFConsignmentsAndMarkDelivery(
              notificationDTO, consignmentUploadedFiles);
        } else {
          log.debug(
              "ignoring the {} for consignment_id: {}.",
              notificationDTO.getEntityName(),
              notificationDTO.getEntityId());
        }
        break;
      default:
        log.info("No event defined for this client {}", notificationDTO);
        break;
    }
  }

  private <T> boolean addEventsToQueue(Collection<T> requestDtos, Queue<T> queue) {
    log.info("Adding events to queue {}", requestDtos);
    return queue.addAll(requestDtos);
  }

  @Scheduled(fixedDelay = 500L)
  public void publishEventsOfHiltiAndProcessErrors() {
    hiltiRequestDtoList.clear();
    int pushedEventCount = eventBuffer.drainTo(hiltiRequestDtoList);

    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    if (pushedEventCount > 0) {
      log.info("Sending requests for {}", hiltiRequestDtoList);
      HiltiResponseDto responseDto =
          objectMapper.convertValue(
              sendRequestToHilti(hiltiRequestDtoList)
                  .orElseThrow(() -> new ZoomException("Unable to get response from Hilti")),
              HiltiResponseDto.class);
      log.info("Response from Hilti: {}", responseDto);

      if (responseDto.getFailCount() > 0) {
        handleFailures(responseDto.getFailureList());
      }
    }
  }

  @Scheduled(fixedDelay = 500L)
  public void publishEventsOfFlipkartAndProcessErrors() {
    clientIntegrationRequestDtoList.clear();
    int pushedEventCount = clientEventBuffer.drainTo(clientIntegrationRequestDtoList);

    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    if (pushedEventCount > 0) {
      /** Calling Flipkart send events Api */
      log.info("Trying to send event request to flipkart {}", clientIntegrationRequestDtoList);
      ClientIntegrationResponseDTO responseDto =
          objectMapper.convertValue(
              sendRequestToFlipkart(clientIntegrationRequestDtoList)
                  .orElseThrow(() -> new ZoomException("Unable to get response from Flipkart")),
              ClientIntegrationResponseDTO.class);
      log.info("Response from Flipkart: {}", responseDto);
    }
  }

  private void handleFailures(List<String> failureList) {
    log.error("Request failed for the following AWBs: {}", failureList);
  }
}
