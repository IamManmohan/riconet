package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.PartnerType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.enums.ZoomUserType;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by ashfakh on 27/10/17.
 */

@Slf4j
public class DepsNotificationServiceTest extends TesterBase {
    @Autowired
    DEPSRecordService depsRecordService;

    @Autowired
    TransportationPartnerMappingRepository transportationPartnerMappingRepository;

    private void processNotification(String str){
        Collection<String> toRecipients;
        Collection<String> ccRecipients;
        Collection<String> bccRecipients;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DEPSNotificationContext context = null;
        List<DEPSNotificationDTO> depsRecordList= new ArrayList<>();
        TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {};
        try {
            depsRecordList = objectMapper.readValue(str, mapType);
        }catch(IOException e){
            log.error("mapping failure",e);
        }
        context = depsRecordService.getNotificationContext(depsRecordList);
        List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
        depsRecordService.sendNotifications(depsNotificationList);

    }

    @Test
    public void prsUnloadingNotification()
    {
        String str ="[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]";
        processNotification(str);
    }

    @Test
    public void tripUnloadingNotification()
    {
        String str ="[{\"id\":2,\"consignmentId\":2,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"TRIP\",\"taskId\":3,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"UNLOADING\"}]";
        processNotification(str);
    }

    @Test
    public void returnScanNotification()
    {
        TransportationPartnerMapping tpm=transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(ZoomTripType.DRS,1l);
        tpm.setPartnerType(PartnerType.MARKET);
        tpm.setPartnerId(null);
        tpm.setUserId(1118l);
        transportationPartnerMappingRepository.save(tpm);
        String str ="[{\"id\":3,\"consignmentId\":4,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"DRS\",\"taskId\":5,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"RETURN_SCAN\"}]";

        processNotification(str);
    }

    @Test
    public void returnScanNotification2()
    {
        TransportationPartnerMapping tpm=transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(ZoomTripType.DRS,1l);
        tpm.setPartnerType(PartnerType.RIVIGO_CAPTAIN);
        tpm.setPartnerId(null);
        tpm.setUserId(2172l);
        transportationPartnerMappingRepository.save(tpm);

        String str ="[{\"id\":3,\"consignmentId\":4,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"DRS\",\"taskId\":5,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"RETURN_SCAN\"}]";
        processNotification(str);
    }

    @Test
    public void returnScanNotification3()
    {
        TransportationPartnerMapping tpm=transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(ZoomTripType.DRS,1l);
        tpm.setPartnerType(PartnerType.BUSINESS_PARTNER);
        tpm.setPartnerId(1l);
        tpm.setUserId(58l);
        transportationPartnerMappingRepository.save(tpm);
        String str ="[{\"id\":3,\"consignmentId\":4,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"DRS\",\"taskId\":5,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"RETURN_SCAN\"}]";
        processNotification(str);
    }

    @Test
    public void returnScanNotification4()
    {
        TransportationPartnerMapping tpm=transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(ZoomTripType.DRS,1l);
        tpm.setPartnerType(PartnerType.VENDOR);
        tpm.setPartnerId(1l);
        tpm.setUserId(1118l);
        transportationPartnerMappingRepository.save(tpm);
        String str ="[{\"id\":3,\"consignmentId\":4,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"DRS\",\"taskId\":5,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"RETURN_SCAN\"}]";
        processNotification(str);
    }

    @Test
    public void instockNotification()
    {
        String str ="[{\"id\":4,\"consignmentId\":3,\"depsType\":\"SHORTAGE\",\"taskId\":25,\"reportedById\":1505,\"inboundLocationId\":20,\"depsTaskType\":\"STOCK_CHECK\"}]";
        processNotification(str);

    }

    @Test
    public void emptyNotification(){
        String str ="[]";
        processNotification(str);

    }


}
