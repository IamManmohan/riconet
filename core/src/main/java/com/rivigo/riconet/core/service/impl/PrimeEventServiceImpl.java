package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.PrimeEventService;
import com.rivigo.riconet.core.service.TripService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.model.Trip;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrimeEventServiceImpl implements PrimeEventService {

  private final ZoomPropertyService zoomPropertyService;

  private final TripService tripService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private List<String> PRIME_RZM_CLIENT_CODE_LIST;
  private List<String> ENABLE_EVENT_TYPES = Collections.emptyList();

  @PostConstruct
  private void init() {
    this.initializePrimeRZMClientCodeList();
    this.initializeEnabledEventTypes();
  }

  @Override
  public void processEvent(PrimeEventDto primeEventDto) {
    log.info("Consuming message: type {}", primeEventDto.getPrimeEventType());
    if (CollectionUtils.isEmpty(primeEventDto.getCwhTrackingList())) {
      log.info("CWH Tracking list is null");
      return; // Skipped event - No retry
    }

    // Validation 1: Consider only zoom client codes
    if (!PRIME_RZM_CLIENT_CODE_LIST.contains(primeEventDto.getClientCode())) {
      log.info(
          "Unrecognized client code {} for prime trip with id: journey id:{}",
          primeEventDto.getClientCode(),
          primeEventDto.getJourneyId());
      return; // Skipped event - No retry
    }

    // Validation 2: check for enabled events: For rollback whenever new event types are enabled for
    // listening
    if (!ENABLE_EVENT_TYPES.contains(primeEventDto.getPrimeEventType())) {
      log.info("Disabled event type: {}", primeEventDto.getPrimeEventType());
      return; // Skipped event - No retry
    }

    // The below validation must be skipped in case of TRIP_EVENT_TRIP_CREATE event
    Trip trip =
        tripService.getTripByPrimeTripCodeAndNotDeleted(primeEventDto.getJourneyId().toString());
    if (trip == null) {
      log.info(
          "Zoom trip not found for prime trip: client: {}, journey id:{}",
          primeEventDto.getClientCode(),
          primeEventDto.getJourneyId());
      // event will be retried after some time
      throw new ZoomException("Zoom trip not found for prime trip");
    }
    if (!trip.getVehicleNumber().equalsIgnoreCase(primeEventDto.getVehicleNumber())) {
      log.info(
          "Vehicle no mismatch: zoom:{}, prime:{}",
          trip.getVehicleNumber(),
          primeEventDto.getVehicleNumber());
      // event will be retried after some time
      throw new ZoomException("Vehicle no mismatch");
    }
    switch (primeEventDto.getPrimeEventType()) {
      case "VEHICLE_EVENT_NODE_IN":
      case "VEHICLE_EVENT_NODE_OUT":
      case "VEHICLE_EVENT_ETA":
        zoomBackendAPIClientService.processVehicleEvent(primeEventDto, trip.getId());
        return;
      default:
        log.info("Unhandled event type: {}", primeEventDto.getPrimeEventType());
        break;
    }
  }

  private void initializePrimeRZMClientCodeList() {
    String primeRZMClientCodeListString =
        zoomPropertyService.getString(ZoomPropertyName.PRIME_RZM_CLIENT_CODE_LIST);
    if (primeRZMClientCodeListString != null) {
      primeRZMClientCodeListString = primeRZMClientCodeListString.replaceAll("\\s+", "");
      if (!primeRZMClientCodeListString.isEmpty()) {
        PRIME_RZM_CLIENT_CODE_LIST = Arrays.asList(primeRZMClientCodeListString.split(","));
      }
    }
    PRIME_RZM_CLIENT_CODE_LIST = Arrays.asList("RZM", "RZMA", "RZMO", "RZMF");
  }

  private void initializeEnabledEventTypes() {
    // Not doing direct string contains as one event name might contain another event name (events
    // A,AB,BA)
    String enabledPrimeEventTypes =
        zoomPropertyService.getString(ZoomPropertyName.ENABLED_PRIME_EVENT_TYPES);
    if (enabledPrimeEventTypes != null) {
      ENABLE_EVENT_TYPES =
          Arrays.stream(enabledPrimeEventTypes.split(","))
              .map(String::trim)
              .collect(Collectors.toList());
    }
  }
}
