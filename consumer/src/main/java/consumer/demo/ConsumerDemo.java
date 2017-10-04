package consumer.demo;

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
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class ConsumerDemo {


  @Autowired
  private DEPSRecordService depsRecordService;

  @Autowired
  ExecutorService executorService;

  private static String bootstrapServers;

  private final AtomicLong offset = new AtomicLong();

  @Async
  private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
    executorService.submit(()->{
      ObjectMapper objectMapper=new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      DEPSNotificationContext context = null;
      try {
        TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {};
        List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(record.value().toString(), mapType);
        context=depsRecordService.getNotificationContext(depsRecordList);
      } catch (IOException e) {

        e.printStackTrace();
      }
      List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
      depsRecordService.sendNotifications(depsNotificationList);
    });
    offset.set(record.offset());
    return CompletableFuture.completedFuture(Done.getInstance());
  }

  private CompletionStage<Long> loadOffset() {
    return CompletableFuture.completedFuture(offset.get());
  }


  public static void main(String[] args) {
    Config config= ConfigFactory.load();
    bootstrapServers=config.getString("akka.kafka.consumer.bootstrap-servers");

    log.info("BootstrapServers----------------------------------------"+bootstrapServers);

    final ActorSystem system = ActorSystem.create("kafka-consumer-demo");
    final ActorMaterializer materializer = ActorMaterializer.create(system);


    ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
    ConsumerDemo consumerDemo=context.getBean(ConsumerDemo.class);





    final ConsumerSettings<String, String> consumerSettings =
      ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


    Set<String> topicSets = new HashSet<>();
    topicSets.add("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION");

//    demo
//      .loadOffset()
//      .thenAccept(fromOffset -> Consumer
//        .plainSource(
//          consumerSettings,
//          Subscriptions.topics(topicSets)
//        )
//        .mapAsync(1, demo::save)
//        .runWith(Sink.ignore(), materializer));

    consumerDemo
      .loadOffset()
      .thenAccept(fromOffset -> Consumer
        .plainSource(
          consumerSettings,
          Subscriptions.assignmentWithOffset(new TopicPartition("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION", 0), fromOffset)
        )
        .mapAsync(1, consumerDemo::save)
        .runWith(Sink.ignore(), materializer));

  }


}
