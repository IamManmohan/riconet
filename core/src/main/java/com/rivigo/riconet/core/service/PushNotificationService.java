package com.rivigo.riconet.core.service;

import java.io.IOException;

/** Created by ashfakh on 21/09/18. */
public interface PushNotificationService {

  void send(String message, String firebaseToken) throws IOException;
}
