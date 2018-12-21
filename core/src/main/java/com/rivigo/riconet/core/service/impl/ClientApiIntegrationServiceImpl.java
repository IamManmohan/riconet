package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.rivigo.riconet.core.constants.ClientConstants;
import com.rivigo.riconet.core.constants.RestUtilConstants;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.client.ClientIntegrationResponseDTO;
import com.rivigo.riconet.core.dto.client.FlipkartLoginResponseDTO;
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
import com.rivigo.riconet.core.enums.HiltiJobType;
import com.rivigo.riconet.core.enums.HiltiStatusCode;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ClientApiIntegrationService;
import com.rivigo.riconet.core.service.ClientConsignmentService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
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
import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentHistoryRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentUploadedFilesRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.common.repository.mysql.UndeliveredConsignmentsRepository;
import com.rivigo.zoom.common.repository.neo4j.LocationRepositoryV2;
import com.rivigo.zoom.exceptions.ZoomException;
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
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientApiIntegrationServiceImpl implements ClientApiIntegrationService {

  @Value("${hilti.update.transactions.url}")
  public String hiltiUpdateTransactionsUrl;

  @Value("${flipkart.login.url}")
  public String flipkartLoginUrl;

  @Value("${flipkart.update.transaction.url}")
  public String flipkartUpdateTransactionUrl;

  @Value("${flipkart.login.username}")
  private String flipkartLoginUsername;

  @Value("${flipkart.login.password}")
  private String flipkartLoginPassword;

  @Value("${flipkart.client.id}")
  private String flipkartClientId;

  @Value("${flipkart.tenant.id}")
  private String flipkartTenantId;

  @Autowired private RestClientUtilityService restClientUtilityService;

  @Autowired private PickupRepository pickupRepository;

  @Autowired private LocationRepositoryV2 locationRepositoryV2;

  @Autowired private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Autowired private ConsignmentUploadedFilesRepository consignmentUploadedFilesRepository;

  @Autowired private ConsignmentHistoryRepository consignmentHistoryRepository;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private UndeliveredConsignmentsRepository undeliveredConsignmentsRepository;

  @Autowired private ClientConsignmentService clientConsignmentService;

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

  private Optional<?> loginToFlipkart() {
    String authString = flipkartLoginUsername + ":" + flipkartLoginPassword;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = "Basic " + new String(authEncBytes);

    HttpHeaders headers = restClientUtilityService.getHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, authStringEnc);
    headers.set(RestUtilConstants.CLIENT_ID, flipkartClientId);
    headers.set(RestUtilConstants.TENANT_ID, flipkartTenantId);

    return restClientUtilityService.executeRest(
        flipkartLoginUrl, HttpMethod.POST, new HttpEntity<>(headers), Object.class);
  }

  private Optional<?> sendRequestToFlipkart(List<FlipkartRequestDTO> requestDtos) {

    /** Calling Flipkart Login Api */
    log.info("Sending login request to flipkart");
    FlipkartLoginResponseDTO loginResponseDto =
        objectMapper.convertValue(
            loginToFlipkart().orElseThrow(() -> new ZoomException("Unable to login to Flipkart")),
            FlipkartLoginResponseDTO.class);
    log.info("Login Response from Flipkart: {}", loginResponseDto);

    HttpHeaders headers = restClientUtilityService.getHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, loginResponseDto.getData().get("access_token"));
    headers.set(RestUtilConstants.CLIENT_ID, flipkartClientId);
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
        consignmentReadOnlyService.findConsignmentByPickupId(notificationDTO.getEntityId());
    Map<String, List<String>> cnoteToBarcodesMap =
        clientConsignmentService.getCnoteToBarcodeMapFromCnoteList(
            cnList.stream().map(ConsignmentReadOnly::getCnote).collect(Collectors.toList()));
    return cnList
        .stream()
        .map(
            v -> {
              PickupDoneDto pickupDoneDto =
                  PickupDoneDto.builder()
                      .pickupTime(TimeUtilsZoom.getTime(pickup.getPickupDate()))
                      .expectedDeliveryDate(TimeUtilsZoom.getDate(v.getPromisedDeliveryDateTime()))
                      .build();
              if (addBarcodes) {
                pickupDoneDto.setBarcodes(cnoteToBarcodesMap.get(v.getCnote()));
              }
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

    switch (notificationDTO.getEventName()) {
      case CN_RECEIVED_AT_OU:
        return IntransitArrivedDto.builder()
            .arrivedAt(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .atDestination("no")
            .build();
      case CN_DELIVERY_LOADED:
        return IntransitArrivedDto.builder()
            .arrivedAt(idToLocationNameMap.getOrDefault(consignment.getLocationId(), ""))
            .atDestination("yes")
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
        throw new ZoomException("Unrecognized intransit event " + notificationDTO);
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
        throw new ZoomException("Unrecognized delivery event " + notificationDTO);
    }
  }

  private BaseHiltiFieldData getFieldDataForCnEvents(
      NotificationDTO notificationDTO, Boolean addBarcodes) {
    BaseHiltiFieldData fieldData;
    switch (notificationDTO.getEventName()) {
      case CN_RECEIVED_AT_OU:
      case CN_DELIVERY_LOADED:
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
        throw new ZoomException("Unrecognized event {}" + notificationDTO);
    }
    fieldData.setTime(TimeUtilsZoom.getTime(new DateTime(notificationDTO.getTsMs())));
    fieldData.setDate(TimeUtilsZoom.getDate(new DateTime(notificationDTO.getTsMs())));
    if (addBarcodes) {
      List<String> barCodes =
          clientConsignmentService.getBarcodeListFromConsignmentId(notificationDTO.getEntityId());
      fieldData.setBarcodes(barCodes);
    }
    return fieldData;
  }

  private List<HiltiRequestDto> getHiltiRequestDtosByType(
      NotificationDTO notificationDTO, Boolean addBarcodes) {

    HiltiJobType jobType;
    HiltiStatusCode statusCode;

    switch (notificationDTO.getEventName()) {
      case PICKUP_COMPLETION:
        return getPickupRequestDtos(notificationDTO, addBarcodes);
      case CN_RECEIVED_AT_OU:
      case CN_DELIVERY_LOADED:
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
        List<HiltiRequestDto> hiltiDtoList = getHiltiRequestDtosByType(notificationDTO, false);
        addEventsToQueue(hiltiDtoList, eventBuffer);
        break;
      case ClientConstants.FLIPKART_SELLER_CLIENT:
      case ClientConstants.FLIPKART_INDIA_CLIENT:
        List<HiltiRequestDto> hiltiRequestDtoListForFlipkart =
            getHiltiRequestDtosByType(notificationDTO, true);
        List<String> cnoteList =
            hiltiRequestDtoListForFlipkart
                .stream()
                .map(HiltiRequestDto::getReferenceNumber)
                .collect(Collectors.toList());
        Map<String, ClientConsignmentMetadata> cnoteToConsignmentMetadataMap =
            clientConsignmentService.getCnoteToConsignmentMetadataMapFromCnoteList(cnoteList);

        List<FlipkartRequestDTO> clientIntegrationRequestDTOList = new ArrayList<>();
        FlipkartRequestDTO clientIntegrationRequestDto;
        for (HiltiRequestDto hiltiRequestDto : hiltiRequestDtoListForFlipkart) {
          clientIntegrationRequestDto = new FlipkartRequestDTO(hiltiRequestDto);
          clientIntegrationRequestDto.setMetaData(
              Optional.ofNullable(
                      cnoteToConsignmentMetadataMap.get(hiltiRequestDto.getReferenceNumber()))
                  .map(ClientConsignmentMetadata::getMetadata)
                  .orElse(Collections.emptyMap()));
          clientIntegrationRequestDTOList.add(clientIntegrationRequestDto);
        }
        addEventsToQueue(clientIntegrationRequestDTOList, clientEventBuffer);
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
      log.info("Trying Send event request to flipkart {}", clientIntegrationRequestDtoList);
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
