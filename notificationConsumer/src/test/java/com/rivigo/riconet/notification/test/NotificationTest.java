package com.rivigo.riconet.notification.test;

import com.rivigo.riconet.core.service.ZoomCommunicationsService;
import com.rivigo.riconet.core.test.TesterBase;
import com.rivigo.riconet.notification.consumer.AppointmentNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DEPSNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DocIssueNotificationConsumer;
import com.rivigo.riconet.notification.consumer.PickupNotificationConsumer;
import com.rivigo.riconet.notification.consumer.ZoomCommunicationsConsumer;
import com.rivigo.riconet.notification.main.NotificationMain;
import com.rivigo.zoom.common.model.mongo.AppointmentNotification;
import com.rivigo.zoom.exceptions.ZoomException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class NotificationTest extends TesterBase {


  @Autowired
  DocIssueNotificationConsumer docIssueNotificationConsumer;

  @Autowired
  DEPSNotificationConsumer depsNotificationConsumer;

  @Autowired
  PickupNotificationConsumer pickupNotificationConsumer;

  @Autowired
  AppointmentNotificationConsumer appointmentNotificationConsumer;

  @Autowired
  ZoomCommunicationsConsumer zoomCommunicationsConsumer;

  @Test
  public void processDepsNotification() throws IOException {
    String str = depsNotificationConsumer.processMessage("[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");

    assertEquals(str, "[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
  }

  @Test
  public void processEmptyDepsNotification() throws IOException {
    String str = depsNotificationConsumer.processMessage("[{\"id\":1,\"consignmentId\":1,\"depsType\":\"SHORTAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"},{\"id\":2,\"consignmentId\":1,\"depsType\":\"DAMAGE\",\"tripId\":1,\"tripType\":\"PRS\",\"taskId\":1,\"reportedById\":50228,\"inboundLocationId\":15,\"depsTaskType\":\"UNLOADING\"}]");
  }

  @Test(expected = Exception.class)
  public void processNotificationException() throws IOException {
    depsNotificationConsumer.processMessage("test");
  }

  @Test
  public void processDocIssueNotification() {
    docIssueNotificationConsumer.processMessage("8|1505|Invoice missing|RECEIVED_AT_OU");
  }

  @Test(expected = ZoomException.class)
  public void processErrorNotification() {
    docIssueNotificationConsumer.processMessage("");
  }

  @Test
  public void processMain() {
    NotificationMain.main(null);
  }

  @Test
  public void processPickupNotificationConsumer() throws IOException {
    String str = "[{\"id\":6,\"lastUpdatedAt\":1510037640211,\"notificationType\":\"PICKUP_REACHED\"}]";
    pickupNotificationConsumer.processMessage(str);
  }


  @Test
  public void processZoomCommunicationPickupCreation() throws IOException {
    String zoomCommunicationPickupCreationString = "{\"confidential\":true,\"message\":\" Time-slot for Pick-up request 308635 changed to 20:00 - 21:00 as Unable to attempt pickup today. For issues, click link: http://bit.ly/2Gwkbnk\",\"phoneNumber\":\"9082132304\",\"userType\":\"CONSIGNER\",\"eventUID\":\"PICKUP_RESCHEDULE_PICKUP_308635_1522845378530\",\"notificationDTO\":\"{\\n  \\\"eventName\\\" : \\\"PICKUP_RESCHEDULE\\\",\\n  \\\"entityId\\\" : 308635,\\n  \\\"entityName\\\" : \\\"PICKUP\\\",\\n  \\\"eventGUID\\\" : \\\"PICKUP_308635\\\",\\n  \\\"tsMs\\\" : 1522845378530,\\n  \\\"eventUID\\\" : \\\"PICKUP_RESCHEDULE_PICKUP_308635_1522845378530\\\",\\n  \\\"metadata\\\" : {\\n    \\\"PICKUP_DATE_TIME\\\" : \\\"1523622840000\\\",\\n    \\\"CLIENT_CODE\\\" : \\\"KitchenRama food service equipment pvt. Ltd-Retail\\\",\\n    \\\"FAILURE_REASON_ID\\\" : \\\"9\\\",\\n    \\\"ORIGIN_FIELD_USER_NAME\\\" : \\\"Naman\\\",\\n    \\\"ORIGIN_FIELD_USER_PHONE\\\" : \\\"9082132304\\\",\\n    \\\"PICKUP_ID\\\" : \\\"308635\\\",\\n    \\\"PICKUP_TIME_SLOT\\\" : \\\"20:00 - 21:00\\\",\\n    \\\"PICKUP_DATE_TIME_STRING\\\" : \\\"13/04/2018\\\",\\n    \\\"CLIENT_ID\\\" : \\\"2519\\\",\\n    \\\"FAILURE_REASON\\\" : \\\"Unable to attempt pickup today\\\",\\n    \\\"LINK\\\" : \\\"http://bit.ly/2Gwkbnk\\\"\\n  },\\n  \\\"subscribers\\\" : [ {\\n    \\\"notificationMode\\\" : \\\"SMS\\\",\\n    \\\"userTypeSubscriberDTO\\\" : {\\n      \\\"userType\\\" : \\\"CONSIGNER\\\",\\n      \\\"subscriberDTO\\\" : {\\n        \\\"userId\\\" : 211,\\n        \\\"userName\\\" : \\\"Naman\\\",\\n        \\\"userEmail\\\" : null,\\n        \\\"phone\\\" : \\\"9082132304\\\",\\n        \\\"subscriberType\\\" : \\\"CONSIGNER\\\",\\n        \\\"mode\\\" : \\\"SMS\\\",\\n        \\\"modeValue\\\" : \\\"9082132304\\\"\\n      }\\n    }\\n  } ],\\n  \\\"conditions\\\" : null\\n}\",\"communicationId\":\"PICKUP_RESCHEDULE_PICKUP_308635_15228453785301148162534\",\"clientResponded\":false,\"urlDTO\":\"{\\n  \\\"shortUrl\\\" : \\\"http://bit.ly/2Gwkbnk\\\",\\n  \\\"longUrl\\\" : \\\"https://zoom-communication.stg.rivigo.com/communication/update-user-response/PICKUP_RESCHEDULE_PICKUP_308635_15228453785301148162534\\\",\\n  \\\"communicationId\\\" : \\\"PICKUP_RESCHEDULE_PICKUP_308635_15228453785301148162534\\\"\\n}\"}";
    zoomCommunicationsConsumer.processMessage(zoomCommunicationPickupCreationString);
  }
}
