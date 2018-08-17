package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.hilti.DeliveryDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryNotDeliveredDto;
import com.rivigo.riconet.core.dto.hilti.DeliveryOFDDto;
import com.rivigo.riconet.core.dto.hilti.HiltiFieldData;
import com.rivigo.riconet.core.dto.hilti.HiltiRequestDto;
import com.rivigo.riconet.core.dto.hilti.HiltiResponseDto;
import com.rivigo.riconet.core.dto.hilti.IntransitArrivedDto;
import com.rivigo.riconet.core.dto.hilti.IntransitDispatchedDto;
import com.rivigo.riconet.core.dto.hilti.PickupDoneDto;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.HiltiJobType;
import com.rivigo.riconet.core.enums.HiltiStatusCode;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.HiltiApiService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HiltiApiServiceImpl implements HiltiApiService {

  @Value("${hilti.update.transactions.url}")
  private String hiltiUpdateTransactionsUrl;

  public static final Long hiltiFixedDelay = 500L;

  @Autowired private RestClientUtilityService restClientUtilityService;

  @Autowired private PickupRepository pickupRepository;

  @Autowired private LocationRepositoryV2 locationRepositoryV2;

  @Autowired private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Autowired private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;

  @Autowired private ConsignmentHistoryRepository consignmentHistoryRepository;

  @Autowired private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private static BlockingQueue<HiltiRequestDto> eventBuffer = new LinkedBlockingQueue<>();

  private List<HiltiRequestDto> lastRequestDtos = new ArrayList<>();

  @SuppressWarnings("unchecked")
  private Optional<?> sendRequestToHilti(List<HiltiRequestDto> requestDtos)
      throws JsonProcessingException {
    return restClientUtilityService.executeRest(
        hiltiUpdateTransactionsUrl,
        HttpMethod.POST,
        new HttpEntity<>(requestDtos, restClientUtilityService.getHeaders()),
        Object.class);
  }

  public List<HiltiRequestDto> getRequestDtosByType(NotificationDTO notificationDTO) {
    HiltiFieldData fieldData;
    HiltiJobType jobType;
    HiltiStatusCode statusCode;
    ConsignmentReadOnly consignment =
        notificationDTO.getEventName() == EventName.PICKUP_COMPLETION
            ? new ConsignmentReadOnly()
            : consignmentReadOnlyService
                .findConsignmentById(notificationDTO.getEntityId())
                .orElseThrow(
                    () -> new ZoomException("Unable to get consignment from {}", notificationDTO));

    Location currentLocation;
    switch (notificationDTO.getEventName()) {
      case PICKUP_COMPLETION:
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
                          .expectedDeliveryDate(
                              TimeUtilsZoom.getDate(v.getPromisedDeliveryDateTime()))
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
      case CN_RECEIVED_AT_OU:
        consignment =
            consignmentReadOnlyService
                .findConsignmentById(notificationDTO.getEntityId())
                .orElseThrow(
                    () -> new ZoomException("Unable to get consignment from {}", notificationDTO));
        currentLocation = locationRepositoryV2.findById(consignment.getLocationId());
        fieldData =
            IntransitArrivedDto.builder()
                .arrivedAt(currentLocation.getName())
                .atDestination(
                    consignment.getLocationId() == consignment.getToLocationId() ? "yes" : "no")
                .build();
        jobType = HiltiJobType.INTRANSIT;
        statusCode = HiltiStatusCode.ARRIVED;
        break;
      case CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU:
        List<Location> locations =
            locationRepositoryV2.findByIdIn(
                Arrays.asList(consignment.getFromLocationId(), consignment.getToLocationId()));
        Map<Long, String> idToLocationNameMap =
            locations.stream().collect(Collectors.toMap(Location::getId, Location::getName));
        fieldData =
            IntransitDispatchedDto.builder()
                .dispatchedFrom(
                    idToLocationNameMap.getOrDefault(consignment.getFromLocationId(), ""))
                .dispatchedTo(idToLocationNameMap.getOrDefault(consignment.getToLocationId(), ""))
                .build();
        jobType = HiltiJobType.INTRANSIT;
        statusCode = HiltiStatusCode.DISPATCHED;
        break;
      case CN_OUT_FOR_DELIVERY:
        fieldData = DeliveryOFDDto.builder().build();
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.OUT_FOR_DELIVERY;
        break;
      case CN_DELIVERY:
        List<ConsignmentUploadedFiles> uploadedDocuments =
            consignmentUploadedFilesRepository.findByConsignmentId(notificationDTO.getEntityId());
        Map<FileTypes, String> uploadedFilesMap =
            uploadedDocuments
                .stream()
                .collect(
                    Collectors.toMap(
                        ConsignmentUploadedFiles::getFileTypes,
                        ConsignmentUploadedFiles::getS3URL));
        fieldData =
            DeliveryDeliveredDto.builder()
                .podDelivered(uploadedFilesMap.getOrDefault(FileTypes.POD, ""))
                .codImage(uploadedFilesMap.getOrDefault(FileTypes.COD_DOD, ""))
                .deliverySignature(uploadedFilesMap.getOrDefault(FileTypes.DELIVERY_CHALLAN, ""))
                .build();
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.DELIVERED;
        break;
      case CN_UNDELIVERY:
        UndeliveredConsignment undeliveredConsignment =
            undeliveredConsignmentsRepository
                .findTop1ByConsignmentIdAndOldDrsIdNotNullOrderByIdDesc(
                    notificationDTO.getEntityId());
        fieldData =
            DeliveryNotDeliveredDto.builder()
                .undeliveryReason(
                    undeliveredConsignment.getReason() + undeliveredConsignment.getSubReason())
                .podUndelivered("")
                .build();
        jobType = HiltiJobType.DELIVERY;
        statusCode = HiltiStatusCode.NOT_DELIVERED;
        break;
      default:
        log.error("Invalid event captured. Unable to process {}", notificationDTO);
        throw new ZoomException("Invalid event captured. Unable to process {}", notificationDTO);
    }
    fieldData.setTime(TimeUtilsZoom.getTime(new DateTime(notificationDTO.getTsMs())));
    fieldData.setDate(TimeUtilsZoom.getDate(new DateTime(notificationDTO.getTsMs())));
    return Collections.singletonList(
        HiltiRequestDto.builder()
            .jobType(jobType.toString())
            .newStatusCode(statusCode.toString())
            .referenceNumber(consignment.getCnote())
            .fieldData(fieldData)
            .build());
  }

  public boolean addEventsToQueue(List<HiltiRequestDto> requestDtos) {
    log.info("Adding events to queue to send to Fareye {}", requestDtos);
    return eventBuffer.addAll(requestDtos);
  }

  //  @Scheduled(fixedDelay = 500L)
  public void publishEventsAndProcessErrors() {
    lastRequestDtos.clear();
    eventBuffer.drainTo(lastRequestDtos);

    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    try {
      HiltiResponseDto responseDto =
          objectMapper.convertValue(
              sendRequestToHilti(lastRequestDtos)
                  .orElseThrow(() -> new ZoomException("Unable to get response from Hilti")),
              HiltiResponseDto.class);
      log.info("Response from Hilti: {}", responseDto);

      if (responseDto.getFailCount() > 0) {
        handleFailures(responseDto.getFailureList());
      }
    } catch (JsonProcessingException e) {
      log.error("Unable to parse JSON ", e);
    }
  }

  private void handleFailures(List<String> failureList) {
    log.error("Request failed for the following AWBs: {}", failureList);
  }
}
