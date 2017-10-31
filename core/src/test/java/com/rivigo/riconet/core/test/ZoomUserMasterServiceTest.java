package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by ashfakh on 30/10/17.
 */

@Slf4j
public class ZoomUserMasterServiceTest extends TesterBase {

    @Autowired
    ZoomUserMasterService zoomUserMasterService;

    @Test(expected = ZoomException.class)
    public void nullChecks1(){
        zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(null,null,null);
    }

    @Test(expected = ZoomException.class)
    public void nullChecks2(){
        zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(null,null,null);
    }

}
