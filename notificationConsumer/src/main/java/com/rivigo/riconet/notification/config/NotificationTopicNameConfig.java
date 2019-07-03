package com.rivigo.riconet.notification.config;

import org.springframework.beans.factory.annotation.Value;

/** Created by ashfakh on 03/07/19. */
public class NotificationTopicNameConfig {

  @Value("${SMS_SINK}")
  private String smsSink;

  @Value("${SMS_SINK_ERROR}")
  private String smsSinkError;

  public String smsSink() {
    return smsSink;
  }

  public String smsSinkError() {
    return smsSinkError;
  }
}
