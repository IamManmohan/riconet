package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.TripService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Created by ashfakh on 30/10/17. */
@Slf4j
public class TripServiceTest extends TesterBase {

  @Autowired TripService tripService;

  @Test
  public void test1() {
    List<Long> tripIdList = new ArrayList<>();
    tripIdList.add(3l);
    tripIdList.add(1l);
    tripIdList.add(1l);
    tripService.getTripsMapByIdIn(tripIdList);
  }
}
