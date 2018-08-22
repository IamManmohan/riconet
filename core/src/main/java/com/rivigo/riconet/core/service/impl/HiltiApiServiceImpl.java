package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.hilti.BaseHiltiFieldData;
import com.rivigo.riconet.core.dto.hilti.DeliveryDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryNotDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryOFDDto;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.riconet.core.dto.hilti.HiltiResponseDto;
import com.rivigo.riconet.core.dto.hilti.IntransitArrivedDto;
import com.rivigo.riconet.core.dto.hilti.IntransitDispatchedDto;
import com.rivigo.riconet.core.dto.hilti.PickupDoneDto;
import com.rivigo.riconet.core.enums.HiltiJobType;
import com.rivigo.riconet.core.enums.HiltiStatusCode;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.HiltiApiService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.ConsignmentUploadedFiles;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.UndeliveredConsignment;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentUploadedFilesRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.mysql.UndeliveredConsignmentsRepository;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HiltiApiServiceImpl implements HiltiApiService {

  @Value("${hilti.update.transactions.url}")
  private String hiltiUpdateTransactionsUrl;

  @Autowired private RestClientUtilityService restClientUtilityService;

  @Autowired private PickupRepository pickupRepository;

  @Autowired private LocationRepositoryV2 locationRepositoryV2;

  @Autowired private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Autowired private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;

  @Autowired private ConsignmentHistoryRepository consignmentHistoryRepository;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private static BlockingQueue<HiltiRequestDto> eventBuffer = new LinkedBlockingQueue<>();

  private List<HiltiRequestDto> lastRequestDtos = new ArrayList<>();

  private Optional<?> sendRequestToHilti(List<HiltiRequestDto> requestDtos) {
    return restClientUtilityService.executeRest(
        hiltiUpdateTransactionsUrl,
        HttpMethod.POST,
        new HttpEntity<>(requestDtos, restClientUtilityService.getHeaders()),
        Object.class);
  }

  private List<HiltiRequestDto> getPickupRequestDtos(NotificationDTO notificationDTO) {
    Pickup pickup = pickupRepository.findOne(notificationDTO.getEntityId());
    List<ConsignmentReadOnly> cnList =
        consignmentReadOnlyService.findConsignmentByPickupId(notificationDTO.getEntityId());
    return cnList
        .stream()
        .map(
            v -> {
              PickupDoneDto pickupDoneDto =
                  PickupDoneDto.builder()
                      .pickupTime(TimeUtilsZoom.getTime(pickup.getPickupDate()))
                      .expectedDeliveryDate(TimeUtilsZoom.getDate(v.getPromisedDeliveryDateTime()))
                      .build();
              pickupDoneDto.setDate(TimeUtilsZoom.getDate(pickup.getPickupDate()));
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
    Optional<ConsignmentReadOnly> consignmentReadOnlyOptional =
        consignmentReadOnlyService
            .findConsignmentById(notificationDTO.getEntityId()) ;
    consignmentReadOnlyOptional.orElseThrow(
                () -> new ZoomException("Unable to get consignment from " + notificationDTO));
    ConsignmentReadOnly consignment = consignmentReadOnlyOptional.get();
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

    switch (notificationDTO.getEventName()) {
      case CN_RECEIVED_AT_OU:
        return IntransitArrivedDto.builder()
            .arrivedAt(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .atDestination(
                BooleanUtils.toStringYesNo(
                    consignment.getLocationId() == consignment.getToLocationId()))
            .build();
      case CN_LOADED:
        return IntransitDispatchedDto.builder()
            .dispatchedFrom(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .dispatchedTo(
                idToLocationNameMap.getOrDefault(
                    nextSchedule.map(ConsignmentSchedule::getLocationId).orElse(null), ""))
            .build();
      default:
        log.error("Unrecognized intransit event {}", notificationDTO);
        throw new ZoomException("Unrecognized intransit event {}", notificationDTO);
    }
  }

  private BaseHiltiFieldData getDeliveryFieldData(NotificationDTO notificationDTO) {
    switch (notificationDTO.getEventName()) {
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
        return DeliveryNotDeliveredDto.builder()
            .undeliveryReason(
                undeliveredConsignment.getReason() + ": " + undeliveredConsignment.getSubReason())
            .podUndelivered("")
            .build();
      default:
        log.error("Unrecognized delivery event {}", notificationDTO);
        throw new ZoomException("Unrecognized delivery event {}", notificationDTO);
    }
  }

  private BaseHiltiFieldData getFieldDataForCnEvents(NotificationDTO notificationDTO) {
    BaseHiltiFieldData fieldData;
    switch (notificationDTO.getEventName()) {
      case CN_RECEIVED_AT_OU:
      case CN_LOADED:
        fieldData = getIntransitFieldData(notificationDTO);
        break;
      case CN_OUT_FOR_DELIVERY:
      case CN_DELIVERY:
      case CN_UNDELIVERY:
        fieldData = getDeliveryFieldData(notificationDTO);
        break;
      default:
        log.error("Unrecognized event {}", notificationDTO);
        throw new ZoomException("Unrecognized event {}", notificationDTO);
    }
    fieldData.setTime(TimeUtilsZoom.getTime(new DateTime(notificationDTO.getTsMs())));
    fieldData.setDate(TimeUtilsZoom.getDate(new DateTime(notificationDTO.getTsMs())));

    return fieldData;
  }

  public List<HiltiRequestDto> getRequestDtosByType(NotificationDTO notificationDTO) {

    HiltiJobType jobType;
    HiltiStatusCode statusCode;

    switch (notificationDTO.getEventName()) {
      case PICKUP_COMPLETION:
        return getPickupRequestDtos(notificationDTO);
      case CN_RECEIVED_AT_OU:
        jobType = HiltiJobType.INTRANSIT;
        statusCode = HiltiStatusCode.ARRIVED;
        break;
      case CN_LOADED:
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
        throw new ZoomException("Invalid event captured. Unable to process {}", notificationDTO);
    }
    String cnote = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.toString());
    if (Strings.isNullOrEmpty(cnote)) {
      cnote =
          consignmentReadOnlyService
              .findConsignmentById(notificationDTO.getEntityId())
              .map(ConsignmentReadOnly::getCnote)
              .orElseThrow(
                  () -> new ZoomException("Unable to get consignment from {}", notificationDTO));
    }
    return Collections.singletonList(
        HiltiRequestDto.builder()
            .jobType(jobType.toString())
            .newStatusCode(statusCode.toString())
            .referenceNumber(cnote)
            .fieldData(getFieldDataForCnEvents(notificationDTO))
            .build());
  }

  public boolean addEventsToQueue(List<HiltiRequestDto> requestDtos) {
    log.info("Adding events to queue to send to Fareye {}", requestDtos);
    return eventBuffer.addAll(requestDtos);
  }

  @Scheduled(fixedDelay = 500L)
  public void publishEventsAndProcessErrors() {
    lastRequestDtos.clear();
    int pushedEventCount = eventBuffer.drainTo(lastRequestDtos);

    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    if (pushedEventCount > 0) {
      log.info("Sending requests for {}", lastRequestDtos);
      HiltiResponseDto responseDto =
          objectMapper.convertValue(
              sendRequestToHilti(lastRequestDtos)
                  .orElseThrow(() -> new ZoomException("Unable to get response from Hilti")),
              HiltiResponseDto.class);
      log.info("Response from Hilti: {}", responseDto);

      if (responseDto.getFailCount() > 0) {
        handleFailures(responseDto.getFailureList());
      }
    }
  }

  private void handleFailures(List<String> failureList) {
    log.error("Request failed for the following AWBs: {}", failureList);
  }
}
