package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.StockAccumulatorService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ashfakh on 30/10/17.
 */
@Slf4j
public class GeneralServiceTest extends TesterBase{

    @Autowired
    ConsignmentService consignmentService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    StockAccumulatorService stockAccumulatorService;

    @Autowired
    LocationService locationService;

    @Autowired
    ZoomPropertyService zoomPropertyService;

    @Test
    public void boxTest() {
        consignmentService.getOriginalNumberOfBoxesByCnote("8075464375");
    }

    @Test
    public void orgIdTest(){
        organizationService.getById(1l);
    }

    @Test
    public void stockAccumulatorTest(){
        stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN,1l, OperationalStatus.ACTIVE);
    }

    @Test
    public void regionSiblingLocationTest(){
        locationService.getAllRegionSiblingsOfLocation("DELT1");
    }

    @Test
    public void zoomPropertyNullTest(){
        zoomPropertyService.getString(ZoomPropertyName.CAPTAIN_APP_LATEST_VERSION);
        zoomPropertyService.getBoolean(ZoomPropertyName.CAPTAIN_APP_LATEST_VERSION,true);
        zoomPropertyService.getBoolean(ZoomPropertyName.CAPTAIN_APP_MINIMUM_VERSION,true);
    }




}
