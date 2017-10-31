package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by ashfakh on 30/10/17.
 */

@Slf4j
public class LocationServiceTest extends TesterBase {

    @Autowired
    LocationService locationService;

    @Test
    public void test(){
        locationService.getPcOrReportingPc(null);
        locationService.getPcOrReportingPc(locationService.getLocationById(35l));
    }

}
