package com.rivigo.riconet.core.consumerabstract;

import akka.Done;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.rivigo.zoom.common.model.mongo.ConsumerMessages;
import com.rivigo.zoom.common.repository.mongo.ConsumerMessagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ashfakh on 26/9/17.
 */


@Slf4j
@Component
public abstract class ConsumerModel {

    private final String topic;

    private final String errorTopic;

    private final Long NUM_RETRIES;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ConsumerMessagesRepository consumerMessagesRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private Timer timer = new HashedWheelTimer();

    private final AtomicLong offset = new AtomicLong();

    public ConsumerModel(String topic, String errorTopic,Long numRetries) {
        this.topic = topic;
        this.errorTopic = errorTopic;
        this.NUM_RETRIES=numRetries;
    }

    @Async
    private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
        if (record.topic().toString().equals(topic)) {
            executorService.submit(() -> {
                try {
                    processMessage(record.value());
                } catch (Exception e) {
                    String errorMsg=getStackTrace(e);
                    processFirstTimeError(record.value(),errorMsg);
                    log.error("First time error", e);
                }
            });
        } else if (record.topic().toString().equals(errorTopic)) {
            executorService.submit(() -> {
                ConsumerMessages consumerMessages = consumerMessagesRepository.findById(record.value());
                try {
                    processMessage(consumerMessages.getMessage());
                } catch (Exception e) {
                    String errorMsg=getStackTrace(e);
                    processError(consumerMessages,errorMsg);
                    log.error("error", e);
                }
            });
        }
        offset.set(record.offset());
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Long> loadOffset() {
        return CompletableFuture.completedFuture(offset.get());
    }

    public abstract String processMessage(String str);

    String processError(ConsumerMessages consumerMessage,String errorMsg) {
        log.error("processing error:" + consumerMessage.getId() + (consumerMessage.getRetryCount()+1L) + errorMsg );
        if (consumerMessage.getRetryCount() < NUM_RETRIES) {
            consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
            consumerMessage.setRetryCount(consumerMessage.getRetryCount() + 1L);
            consumerMessage.setErrorMsg(consumerMessage.getErrorMsg()+", Retry number "+consumerMessage.getRetryCount().toString()+" "+errorMsg);
            consumerMessagesRepository.save(consumerMessage);
            ConsumerTimer task = new ConsumerTimer(consumerMessage.getId(), errorTopic, kafkaTemplate);
            timer.newTimeout(task, 5 * (consumerMessage.getRetryCount()), TimeUnit.MINUTES);
        }
        return consumerMessage.getMessage();
    }

    String processFirstTimeError(String str,String errorMsg) {
        log.error(" Processing first time error" + errorMsg);
        ConsumerMessages consumerMessage = new ConsumerMessages();
        consumerMessage.setId(topic + DateTime.now().getMillis());
        consumerMessage.setMessage(str);
        consumerMessage.setRetryCount(1L);
        consumerMessage.setTopic(topic);
        consumerMessage.setCreatedAt(DateTime.now().getMillis());
        consumerMessage.setLastUpdatedAt(DateTime.now().getMillis());
        consumerMessage.setErrorMsg("1"+errorMsg);

        consumerMessagesRepository.save(consumerMessage);
        ConsumerTimer task = new ConsumerTimer(consumerMessage.getId(), errorTopic, kafkaTemplate);
        timer.newTimeout(task, 5, TimeUnit.MINUTES);

        return str;
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


    public void load(ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
        Set<String> topics = new HashSet<>();
        topics.add(topic);
        topics.add(errorTopic);


        this.loadOffset()
                .thenAccept(fromOffset -> Consumer
                        .plainSource(
                                consumerSettings,
                                Subscriptions.topics(topics)
                        )
                        .mapAsync(1, this::save)
                        .runWith(Sink.ignore(), materializer));
    }
}