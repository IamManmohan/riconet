package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 27/10/17.
 */

@Slf4j
public class DepsNotificationServiceTest extends TesterBase {

    @Autowired
    DEPSRecordService depsRecordService;

    @Test
    public void processNotification()
    {
        String str ="[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]";
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
        assertEquals(str,"[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
    }
}
