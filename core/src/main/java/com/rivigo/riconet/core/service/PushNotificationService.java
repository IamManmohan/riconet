package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.enums.ApplicationId;
import java.io.IOException;
import org.json.JSONObject;

/** Created by ashfakh on 21/09/18. */
public interface PushNotificationService {

  void send(JSONObject message, String firebaseToken, String priority, ApplicationId applicationId)
      throws IOException;
}
