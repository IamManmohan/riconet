package com.rivigo.riconet.event.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.event.consumer.ConsignmentCompletionPickupChargesActionConsumer;
import com.rivigo.riconet.event.consumer.ZoomEventTriggerConsumer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by ashfakh on 12/05/18.
 */
@Component
@Slf4j
public class EventMain {

  @Autowired
  private ZoomEventTriggerConsumer zoomEventTriggerConsumer;

  @Autowired
  private ConsignmentCompletionPickupChargesActionConsumer consignmentCompletionPickupChargesActionConsumer;

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("events");
    final ActorMaterializer materializer = ActorMaterializer.create(system);
    ApplicationContext context = new AnnotationConfigApplicationContext(
        ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
    EventMain consumer = context.getBean(EventMain.class);
    Config config = ConfigFactory.load();
    String bootstrapServers = config.getString("bootstrap.servers");
    log.info("Bootstrap servers are: {}", bootstrapServers);
    String groupId = config.getString("group.id");
    String groupConsignmentCompletionPickupCharges=config.getString("group.consignmentCompletionPickupCharges");
    final ConsumerSettings<String, String> defaultConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(groupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    final ConsumerSettings<String, String> consignmentCompletionPickupChargesConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(groupConsignmentCompletionPickupCharges)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    consumer.loadDefault(materializer, defaultConsumerSettings);
    consumer.loadConsignmentCompletionPickupCharges(materializer,consignmentCompletionPickupChargesConsumerSettings);
  }

  public void loadDefault(ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
    zoomEventTriggerConsumer.load(materializer, consumerSettings);
  }

  public void loadConsignmentCompletionPickupCharges(ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
    consignmentCompletionPickupChargesActionConsumer.load(materializer, consumerSettings);
  }
}
