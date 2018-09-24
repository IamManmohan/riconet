package com.rivigo.riconet.core.service;

import java.io.IOException;
import org.json.JSONObject;

/** Created by ashfakh on 21/09/18. */
public interface PushNotificationService {

  void send(JSONObject message, String firebaseToken, String priority) throws IOException;
}
