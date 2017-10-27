package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.TripStatus;
import com.rivigo.zoom.common.model.Trip;
import com.rivigo.zoom.common.repository.mysql.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public Map<Long, Trip> getTripsMapByIdIn(Collection<Long> tripIdList) {
        List<Trip> tripList = getTripsByIdIn(tripIdList);
        Map<Long, Trip> idToTripMap = new HashMap<>();
        for (Trip t : tripList) {
            if(TripStatus.DELETED.equals(t.getStatus())){
                continue;
            }
            Trip o = idToTripMap.get(t.getId());
            if (o == null) {
                idToTripMap.put(t.getId(), t);
            }
        }
        return idToTripMap;
    }

    public List<Trip> getTripsByIdIn(Collection<Long> linehaulTripIds) {
        return new ArrayList<>((List)(tripRepository.findAll(linehaulTripIds)));
    }
}
