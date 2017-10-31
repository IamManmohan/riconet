package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.zoom.common.model.ClientUser;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.repository.mysql.ClientUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by ashfakh on 30/10/17.
 */

@Slf4j
public class ConsignmentServiceTest extends TesterBase {

    @Autowired
    ConsignmentService consignmentService;

    @Test
    public void nullChecks(){
        consignmentService.getConsignmentsByIds(null);
        consignmentService.getConsignmentsByIds(new ArrayList<>());
    }

}
