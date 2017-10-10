package depsNotification;

import ConsumerAbstract.ConsumerModel;
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
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsumerMessages;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.repository.mysql.ConsumerMessagesRepository;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import config.ServiceConfig;
import enums.ProducerTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class DEPSNotificationConsumer extends ConsumerModel {


  @Autowired
  private DEPSRecordService depsRecordService;

  public String processMessage(String str){
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    DEPSNotificationContext context = null;
    try {
      TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {};
      List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(str, mapType);
      context = depsRecordService.getNotificationContext(depsRecordList);
    }catch (Exception e){
      e.printStackTrace();
    }
    return str;
  }

  public DEPSNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name());
  }
}
