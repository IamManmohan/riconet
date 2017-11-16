package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.enums.PartnerType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;




@Slf4j
public class PickupServiceTest extends TesterBase {
    @Autowired
    PickupService pickupService;

    public String processMessage(String str){
        DEPSNotificationContext context = null;
        List<PickupNotificationDTO> pickupNotificationDTOList=null;
        try {
            TypeReference<List<PickupNotificationDTO>> mapType = new TypeReference<List<PickupNotificationDTO>>() {};
            ObjectMapper objectMapper =new ObjectMapper();
            pickupNotificationDTOList= objectMapper.readValue(str, mapType);
        }catch (Exception e){
            log.error("DepsNotification mapping failed", e);
        }
        pickupService.processPickupNotificationDTOList(pickupNotificationDTOList);
        return str;
    }

    @Test
    public void createPickup()
    {
        String str ="[{\"id\":1,\"lastUpdatedAt\":1510036034224,\"notificationType\":\"PICKUP_CREATED\"}]";
        processMessage(str);
    }

    @Test
    public void createPickupBpAutoAssign()
    {
        String str ="[{\"id\":2,\"lastUpdatedAt\":1510036961494,\"notificationType\":\"PICKUP_CREATED\"}]";
        processMessage(str);
    }

    @Test
    public void AssignBp()
    {
        String str ="[{\"id\":3,\"lastUpdatedAt\":1510036961494,\"notificationType\":\"PICKUP_ASSIGNED\"}]";
        processMessage(str);
    }

    @Test
    public void AssignBpCaptain()
    {
        String str ="[{\"id\":4,\"lastUpdatedAt\":1510036961494,\"notificationType\":\"PICKUP_ASSIGNED\"}]";
        processMessage(str);
    }

    @Test
    public void AssignZoomUser()
    {
        String str ="[{\"id\":5,\"lastUpdatedAt\":1510037019087,\"notificationType\":\"PICKUP_ASSIGNED\"}]";
        processMessage(str);
    }

    @Test
    public void markPickupReached()
    {
        String str ="[{\"id\":6,\"lastUpdatedAt\":1510037640211,\"notificationType\":\"PICKUP_REACHED\"}]";
        processMessage(str);
    }

    @Test
    public void initiateDelayedPickupsNotification()
    {
        String str ="[{\"lastUpdatedAt\":1510037640211,\"notificationType\":\"PICKUP_DELAYED\"}]";
        processMessage(str);
    }

}
