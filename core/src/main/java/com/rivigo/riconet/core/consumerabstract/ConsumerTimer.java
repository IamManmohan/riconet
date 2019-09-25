package com.rivigo.riconet.core.consumerabstract;

import lombok.AllArgsConstructor;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.springframework.kafka.core.KafkaTemplate;

/** Created by ashfakh on 9/10/17. */
@AllArgsConstructor
public class ConsumerTimer implements TimerTask {

  private final String msgPayload;

  private final String topic;

  private KafkaTemplate kafkaTemplate;

  @Override
  public void run(Timeout timeout) {
    kafkaTemplate.send(topic, msgPayload);
  }
}
