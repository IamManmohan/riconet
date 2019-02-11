package com.rivigo.riconet.core.consumerabstract;

import akka.Done;
import akka.NotUsed;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerMessage;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import com.rivigo.zoom.common.model.mongo.ConsumerMessages;
import com.rivigo.zoom.common.repository.mongo.ConsumerMessagesRepository;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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

  public static final Long NUM_RETRIES = 5l;

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

//  private final AtomicLong offset = new AtomicLong();

  @Autowired private ExecutorService executorService;

  @Autowired private ConsumerMessagesRepository consumerMessagesRepository;

  @Autowired private KafkaTemplate kafkaTemplate;

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
                  log.error("First time error", e);
                }
              });
    } else if (record.topic().equals(getErrorTopic())) {
      executorService.submit(
              () -> {
                ConsumerMessages consumerMessages = consumerMessagesRepository.findById(record.value());
                try {
                  processMessage(consumerMessages.getMessage());
                } catch (Exception e) {
                  String errorMsg = getStackTrace(e);
                  processError(consumerMessages, errorMsg);
                  log.error("error", e);
                }
              });
    }
//    offset.set(record.offset());
    return CompletableFuture.completedFuture(Done.getInstance());
  }

//  private CompletionStage<Long> loadOffset() {
//    return CompletableFuture.completedFuture(offset.get());
//  }

  public abstract void processMessage(String str) throws IOException;

  String processError(ConsumerMessages consumerMessage, String errorMsg) {
    log.error(
            "processing error:"
                    + consumerMessage.getId()
                    + (consumerMessage.getRetryCount() + 1L)
                    + errorMsg);
    if (consumerMessage.getRetryCount() < getNumRetries()) {
      consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
      consumerMessage.setRetryCount(consumerMessage.getRetryCount() + 1L);
      consumerMessage.setErrorMsg(
              consumerMessage.getErrorMsg()
                      + ", Retry number "
                      + consumerMessage.getRetryCount().toString()
                      + " "
                      + errorMsg);
      consumerMessagesRepository.save(consumerMessage);
      ConsumerTimer task =
              new ConsumerTimer(consumerMessage.getId(), getErrorTopic(), kafkaTemplate);
      timer.newTimeout(task, 5 * (consumerMessage.getRetryCount()), TimeUnit.MINUTES);
    }
    return consumerMessage.getMessage();
  }

  String processFirstTimeError(String str, String errorMsg) {
    log.error(" Processing first time error" + errorMsg);
    ConsumerMessages consumerMessage = new ConsumerMessages();
    consumerMessage.setId(getTopic() + DateTime.now().getMillis());
    consumerMessage.setMessage(str);
    consumerMessage.setRetryCount(1L);
    consumerMessage.setTopic(getTopic());
    consumerMessage.setCreatedAt(DateTime.now().getMillis());
    consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
    consumerMessage.setErrorMsg("1" + errorMsg);

    consumerMessagesRepository.save(consumerMessage);
    ConsumerTimer task = new ConsumerTimer(consumerMessage.getId(), getErrorTopic(), kafkaTemplate);
    timer.newTimeout(task, 5, TimeUnit.MINUTES);

    return str;
  }

  public void load(
          ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
    Set<String> topics = new HashSet<>();
    topics.add(getTopic());
    topics.add(getErrorTopic());

    Consumer.committableSource(consumerSettings, Subscriptions.topics(topics))
            .mapAsync(
                    1,
                    msg ->
                            save(msg.record())
                                    .thenApply(done -> msg.committableOffset()))
            .batch(
                    20,
                    ConsumerMessage::createCommittableOffsetBatch,
                    ConsumerMessage.CommittableOffsetBatch::updated
            )
            .mapAsync(3, c -> c.commitJavadsl())
            .to(Sink.ignore())
            .run(materializer);
//    this.loadOffset()
//        .thenAccept(
//            fromOffset ->
//                Consumer.plainSource(consumerSettings, Subscriptions.topics(topics))
//                    .mapAsync(1, this::save)
//                    .runWith(Sink.ignore(), materializer));
  }
}