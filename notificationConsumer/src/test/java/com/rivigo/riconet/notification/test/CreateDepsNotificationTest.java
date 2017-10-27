package com.rivigo.riconet.notification.test;

import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.notification.consumer.DEPSNotificationConsumer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class CreateDepsNotificationTest extends TesterBase{


    @Autowired
    DEPSNotificationConsumer depsNotificationConsumer;

    @Test
    public void processNotification()
    {
        String str =depsNotificationConsumer.processMessage("[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");

        assertEquals(str,"[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
    }

    @Test(expected = Exception.class)
    public void processNotificationException()
    {
        depsNotificationConsumer.processMessage("test");
    }


}
