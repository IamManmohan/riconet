package com.rivigo.riconet.core.consumer;

import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 08/08/19. */
@Slf4j
@Component
public class HealthCheckConsumer extends ConsumerModel {

  @Value("${HEALTH_CHECK_TOPIC}")
  private String healthCheckTopic;

  @Value("${HEALTH_CHECK_TOPIC_ERROR}")
  private String healthCheckTopicError;

  @Override
  public String getTopic() {
    return healthCheckTopic;
  }

  @Override
  public String getErrorTopic() {
    return healthCheckTopicError;
  }

  @Override
  public void processMessage(String str) {
    log.info("Processing message in Health check consumer {}", str);
  }
}
