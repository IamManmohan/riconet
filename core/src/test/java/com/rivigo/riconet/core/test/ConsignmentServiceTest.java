package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.ConsignmentService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Created by ashfakh on 30/10/17. */
@Slf4j
public class ConsignmentServiceTest extends TesterBase {

  @Autowired ConsignmentService consignmentService;

  @Test
  public void nullChecks() {
    consignmentService.getConsignmentsByIds(null);
    consignmentService.getConsignmentsByIds(new ArrayList<>());
  }
}
