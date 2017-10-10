//package depsNotification;
//
//import akka.Done;
//import akka.actor.ActorSystem;
//import akka.kafka.ConsumerSettings;
//import akka.kafka.Subscriptions;
//import akka.kafka.javadsl.Consumer;
//import akka.stream.ActorMaterializer;
//import akka.stream.javadsl.Sink;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rivigo.zoom.common.config.ZoomConfig;
//import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
//import com.rivigo.zoom.common.dto.DEPSNotificationContext;
//import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
//import com.rivigo.zoom.common.model.ConsumerMessages;
//import com.rivigo.zoom.common.model.mongo.DEPSNotification;
//import com.rivigo.zoom.common.repository.mysql.ConsumerMessagesRepository;
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//import config.ServiceConfig;
//import enums.ProducerTopics;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.joda.time.DateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import service.DEPSRecordService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Slf4j
//@Component
//public class DepsNotificationConsumer {
//
//
//  @Autowired
//  private DEPSRecordService depsRecordService;
//
//  @Autowired
//  ExecutorService executorService;
//
//  @Autowired
//  ConsumerMessagesRepository consumerMessagesRepository;
//
//  private static String bootstrapServers;
//
//  private final AtomicLong offset = new AtomicLong();
//
//  @Async
//  private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
//    Boolean processCheck=false;
//    executorService.submit(()->{
//      try {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        DEPSNotificationContext context = null;
//        try {
//          TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {
//          };
//          List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(record.value().toString(), mapType);
//          context = depsRecordService.getNotificationContext(depsRecordList);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
//        depsRecordService.sendNotifications(depsNotificationList);
//      } catch (Exception e) {
//        log.error("--------------failed----------------");
//          ConsumerMessages consumerMessage;
//          consumerMessage=consumerMessagesRepository.findByMessage(record.value());
//          if(consumerMessage==null){
//            consumerMessage=new ConsumerMessages();
//            consumerMessage.setMessage(record.value());
//            consumerMessage.setRetry_count(1L);
//            consumerMessage.setRetry_time(DateTime.now().getMillis());
//            consumerMessage.setTopic(ProducerTopics.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.toString());
//          }else{
//            consumerMessage.setRetry_count(consumerMessage.getRetry_count()+1L);
//          }
//          consumerMessagesRepository.save(consumerMessage);
//        e.printStackTrace();
//      }
//    });
//    offset.set(record.offset());
//    return CompletableFuture.completedFuture(Done.getInstance());
//  }
//
//  private CompletionStage<Long> loadOffset() {
//    return CompletableFuture.completedFuture(offset.get());
//  }
//
//
//  public static void main(String[] args) {
//    Config config= ConfigFactory.load();
//    bootstrapServers=config.getString("bootstrap-servers");
//
//    log.info("BootstrapServers----------------------------------------"+bootstrapServers);
//
//    final ActorSystem system = ActorSystem.create("kafka-consumer-depsNotification");
//    final ActorMaterializer materializer = ActorMaterializer.create(system);
//
//
//    ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
//    DepsNotificationConsumer depsNotificationConsumer =context.getBean(DepsNotificationConsumer.class);
//
//
//
//
//
//    final ConsumerSettings<String, String> consumerSettings =
//      ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
//        .withBootstrapServers(bootstrapServers)
//        .withGroupId("group1")
//        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//
//    depsNotificationConsumer
//      .loadOffset()
//      .thenAccept(fromOffset -> Consumer
//        .plainSource(
//          consumerSettings,
//          Subscriptions.assignmentWithOffset(new TopicPartition(ProducerTopics.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.toString(), 0), fromOffset)
//        )
//        .mapAsync(1, depsNotificationConsumer::save)
//        .runWith(Sink.ignore(), materializer));
//
//  }
//
//
//}
