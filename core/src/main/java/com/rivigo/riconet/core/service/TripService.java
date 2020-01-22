package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.model.Trip;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface TripService {

  Map<Long, Trip> getTripsMapByIdIn(Collection<Long> tripIdList);

  List<Trip> getTripsByIdIn(Collection<Long> linehaulTripIds);

  Trip getTripByPrimeTripCodeAndNotDeleted(String tripCode);
}
