package consumer.depsNotification;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.ConsumerMessages;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import config.ServiceConfig;
import dto.TestDTO;
import enums.ProducerTopics;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
@Getter
@Setter
public abstract class ConsumerModel {

    private Topic topic;

    @Autowired
    private DEPSRecordService depsRecordService;

    @Autowired
    ExecutorService executorService;

    private Timer timer=new HashedWheelTimer();

    //@Autowired
    //private KafkaTemplate kafkaTemplate;

    private final AtomicLong offset = new AtomicLong();

    @Async
    private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
        executorService.submit(()->{
            processMeassage(record.value());
        });
        offset.set(record.offset());
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Long> loadOffset() {
        return CompletableFuture.completedFuture(offset.get());
        }

    public abstract String processMeassage(String str);

    public abstract String getTopic();

    public void load() {
        final ActorSystem system = ActorSystem.create("kafka-consumer-depsNotification");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        ConsumerModel consumerModel=context.getBean(ConsumerModel.class);



        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        String Topic=getTopic();

        consumerModel
                .loadOffset()
                .thenAccept(fromOffset -> Consumer
                        .plainSource(
                                consumerSettings,
                                Subscriptions.assignmentWithOffset(new TopicPartition("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION", 0), fromOffset)
                        )
                        .mapAsync(1, consumerModel::save)
                        .runWith(Sink.ignore(), materializer));
    }
}


