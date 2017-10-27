package com.rivigo.riconet.notificationConsumer.test.service;

import com.rivigo.zoom.common.repository.mysql.CityRepository;
import com.rivigo.riconet.notificationConsumer.depsNotification.DEPSNotificationConsumer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.riconet.core.test.TesterBase;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class CreateDepsNotificationTest extends TesterBase{


    @Autowired
    CityRepository cityRepository;

    @Autowired
    DEPSRecordService depsRecordService;

    @Autowired
    DEPSNotificationConsumer depsNotificationConsumer;

    @Test
    public void processNotification()
    {
        String str =depsNotificationConsumer.processMessage("[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");

        assertEquals(str,"[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
    }

//    @Test
//    public void processNotificationException()
//    {
//        City city=new City();
//        cityRepository.save(city);
//        assert city.getId()!= null;
//    }

}
