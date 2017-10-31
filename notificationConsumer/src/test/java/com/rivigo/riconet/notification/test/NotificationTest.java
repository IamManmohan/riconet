package com.rivigo.riconet.notification.test;

import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.notification.consumer.DEPSNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DocIssueNotificationConsumer;
import com.rivigo.riconet.notification.main.NotificationMain;
import com.rivigo.zoom.exceptions.ZoomException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class NotificationTest extends TesterBase{


    @Autowired
    DocIssueNotificationConsumer docIssueNotificationConsumer;

    @Autowired
    DEPSNotificationConsumer depsNotificationConsumer;

    @Test
    public void processDepsNotification()
    {
        String str =depsNotificationConsumer.processMessage("[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");

        assertEquals(str,"[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
    }

    @Test(expected = Exception.class)
    public void processNotificationException()
    {
        depsNotificationConsumer.processMessage("test");
    }

    @Test
    public void processDocIssueNotification()
    {
        docIssueNotificationConsumer.processMessage("8|1505|Invoice missing|RECEIVED_AT_OU");
    }

    @Test(expected = ZoomException.class)
    public void processErrorNotification()
    {
        docIssueNotificationConsumer.processMessage("");
    }

    @Test
    public void processMain(){
        NotificationMain.main(null);
    }
}
