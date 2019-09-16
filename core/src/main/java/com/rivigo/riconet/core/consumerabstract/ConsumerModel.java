package com.rivigo.riconet.core.consumerabstract;

import static java.util.concurrent.TimeUnit.MINUTES;

import akka.Done;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ConsumerMessage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 26/9/17. */
@Slf4j
@Component
public abstract class ConsumerModel {

  private static final Long DELAY_VALUE = 5L;
  private static final TimeUnit TIME_UNIT = MINUTES;
  private static final Long NUM_RETRIES = 5L;

  public abstract String getTopic();

  public abstract String getErrorTopic();

  /**
   * For all the consumers default retries are 5. One can change this number by overriding this in
   * the respective consumer
   *
   * @return
   */
  public Long getNumRetries() {
    return NUM_RETRIES;
  }

  @Autowired private ExecutorService executorService;

  @Autowired private KafkaTemplate kafkaTemplate;

  @Autowired private ObjectMapper objectMapper;

  private Timer timer = new HashedWheelTimer();

  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  @Async
  public CompletionStage<Done> save(ConsumerRecord<String, String> record) {
    if (record.topic().equals(getTopic())) {
      executorService.submit(
          () -> {
            try {
              processMessage(record.value());
            } catch (Exception e) {
              String errorMsg = getStackTrace(e);
              processFirstTimeError(record.value(), errorMsg);
            }
          });
    } else if (record.topic().equals(getErrorTopic())) {
      executorService.submit(
          () -> {
            ConsumerMessage consumerMessage = null;
            try {
              consumerMessage = objectMapper.readValue(record.value(), ConsumerMessage.class);
              processMessage(consumerMessage.getMessage());
            } catch (Exception e) {
              String errorMsg = getStackTrace(e);
              processError(consumerMessage, errorMsg);
              log.error("error", e);
            }
          });
    }
    return CompletableFuture.completedFuture(Done.getInstance());
  }

  public abstract void processMessage(String str) throws IOException;

  private void processError(ConsumerMessage consumerMessage, String errorMsg) {
    log.error("Processing error for payload {}", consumerMessage.toString());
    if (consumerMessage.getRetryCount() < getNumRetries()) {
      consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
      consumerMessage.setRetryCount(consumerMessage.getRetryCount() + 1L);
      consumerMessage.setErrorMsg(
          consumerMessage.getErrorMsg()
              + ", Retry number "
              + consumerMessage.getRetryCount().toString()
              + " "
              + errorMsg);
      createTimerTask(consumerMessage);
    }
  }

  private void processFirstTimeError(String message, String errorMsg) {
    log.error("Processing first time error: {}", errorMsg);
    ConsumerMessage consumerMessage = new ConsumerMessage();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    consumerMessage.setId(getTopic() + uuid);
    consumerMessage.setMessage(message);
    consumerMessage.setRetryCount(1L);
    consumerMessage.setTopic(getTopic());
    consumerMessage.setCreatedAt(DateTime.now().getMillis());
    consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
    consumerMessage.setErrorMsg("1" + errorMsg);
    createTimerTask(consumerMessage);
  }

  /**
   * NOTE: This method creates an in-memory timer task which doesn't guarantee retries for the given
   * no. of times.
   */
  private void createTimerTask(ConsumerMessage consumerMessage) {
    try {
      String consumerMessageString = objectMapper.writeValueAsString(consumerMessage);
      log.info(
          "Creating a timer task for a delay of {} {} for payload: {}",
          DELAY_VALUE,
          TIME_UNIT,
          consumerMessageString);
      ConsumerTimer task = new ConsumerTimer(consumerMessageString, getErrorTopic(), kafkaTemplate);
      timer.newTimeout(task, DELAY_VALUE * (consumerMessage.getRetryCount()), TIME_UNIT);
    } catch (Exception e) {
      log.error("Error in creating and pushing ConsumerTimer object to kafka: {}", e);
    }
  }

  public void load(
      ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
    log.info(
        "Loading Consumer {} with source topic : {} and error topic {}",
        consumerSettings.getProperty("group.id"),
        getTopic(),
        getErrorTopic());
    Set<String> topics = new HashSet<>();
    topics.add(getTopic());
    topics.add(getErrorTopic());

    Consumer.plainSource(consumerSettings, Subscriptions.topics(topics))
        .mapAsync(1, this::save)
        .runWith(Sink.ignore(), materializer);
  }
}
