package com.rivigo.riconet.notification.test;

import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.notification.consumer.DocIssueNotificationConsumer;
import com.rivigo.riconet.notification.main.NotificationMain;
import com.rivigo.zoom.exceptions.ZoomException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class DocIssueNotificationTest extends TesterBase{


    @Autowired
    DocIssueNotificationConsumer docIssueNotificationConsumer;

    @Test
    public void processNotification()
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
