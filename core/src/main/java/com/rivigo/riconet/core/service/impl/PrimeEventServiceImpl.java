package com.rivigo.riconet.core.service.impl;

import static com.rivigo.zoom.common.enums.ZoomPropertyName.ENABLED_PRIME_EVENT_TYPES;

import com.rivigo.riconet.core.dto.primesync.PrimeEventBaseDto;
import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;
import com.rivigo.riconet.core.service.PrimeEventService;
import com.rivigo.riconet.core.service.TripService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.model.Trip;
import com.rivigo.zoom.common.service.ZoomPropertyService;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrimeEventServiceImpl implements PrimeEventService {

  private final ObjectMapper objectMapper;

  private final ZoomPropertyService zoomPropertyService;

  private final TripService tripService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Override
  public void processEvent(PrimeEventBaseDto primeEventBaseDto) throws IOException {
    log.info(
        "Consuming message: type {}, timestamp: {}",
        primeEventBaseDto.getEventType(),
        primeEventBaseDto.getEventTimestamp());
    PrimeEventDto primeEventDto =
        objectMapper.readValue(primeEventBaseDto.getMessage(), PrimeEventDto.class);
    if (CollectionUtils.isEmpty(primeEventDto.getCwhTrackingList())) {
      log.info("CWH Tracking list is null");
      return; // Skipped event - No retry
    }

    // Validation 1: Consider only zoom client codes
    if (!this.getPrimeRZMClientCodeList().contains(primeEventDto.getClientCode())) {
      log.info(
          "Unrecognized client code {} for prime trip with id: journey id:{}",
          primeEventDto.getClientCode(),
          primeEventDto.getJourneyId());
      return; // Skipped event - No retry
    }

    // Validation 2: check for enabled events: For rollback whenever new event types are enabled for
    // listening
    String enabledPrimeEventTypes =
        zoomPropertyService.getPropertyValue(String.class, ENABLED_PRIME_EVENT_TYPES, null);
    if (!validateEventType(enabledPrimeEventTypes, primeEventDto.getPrimeEventType())) {
      log.info(
          "Disabled event type: {}, event: {}", primeEventDto.getPrimeEventType(), primeEventDto);
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
    if (!trip.getVehicleNumber().equals(primeEventDto.getVehicleNumber())) {
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
      default:
        log.info(
            "Unhandled event type: {}, event: {}",
            primeEventDto.getPrimeEventType(),
            primeEventDto);
        break;
    }
  }

  private List<String> getPrimeRZMClientCodeList() {
    String primeRZMClientCodeListString = this.getPrimeRZMClientCodeListString();
    return Arrays.asList(primeRZMClientCodeListString.split(","));
  }

  private String getPrimeRZMClientCodeListString() {
    String primeRZMClientCodeListString =
        zoomPropertyService.getPropertyValue(
            String.class, ZoomPropertyName.PRIME_RZM_CLIENT_CODE_LIST, null);
    if (primeRZMClientCodeListString != null) {
      primeRZMClientCodeListString = primeRZMClientCodeListString.replaceAll("\\s+", "");
      if (!primeRZMClientCodeListString.isEmpty()) return primeRZMClientCodeListString;
    }
    return "RZM,RZMA,RZMO,RZMF";
  }

  private boolean validateEventType(
      @Nullable String enabledPrimeEventTypes, String primeEventType) {
    if (enabledPrimeEventTypes != null) {
      List<String> enableEventTypes = new ArrayList<>();
      for (String s : enabledPrimeEventTypes.split(",")) {
        enableEventTypes.add(s.trim()); // Trim to avoid issues due to spaces
      }
      // Not doing direct string contains as one event name might contain another event name (events
      // A,AB,BA)
      return enableEventTypes.contains(primeEventType);
    }
    return false;
  }
}
