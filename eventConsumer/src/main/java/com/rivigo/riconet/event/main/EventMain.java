package com.rivigo.riconet.event.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.event.consumer.BfPickupChargesActionConsumer;
import com.rivigo.riconet.event.consumer.ConsignmentBlockUnblockConsumer;
import com.rivigo.riconet.event.consumer.ZoomEventTriggerConsumer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 12/05/18. */
@Component
@Slf4j
public class EventMain {

  private final ZoomEventTriggerConsumer zoomEventTriggerConsumer;

  private final ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer;

  private final BfPickupChargesActionConsumer bfPickupChargesActionConsumer;

  public EventMain(
      ZoomEventTriggerConsumer zoomEventTriggerConsumer,
      ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer,
      BfPickupChargesActionConsumer bfPickupChargesActionConsumer) {
    this.zoomEventTriggerConsumer = zoomEventTriggerConsumer;
    this.consignmentBlockUnblockConsumer = consignmentBlockUnblockConsumer;
    this.bfPickupChargesActionConsumer = bfPickupChargesActionConsumer;
  }

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("events");
    final ActorMaterializer materializer = ActorMaterializer.create(system);
    ApplicationContext context =
        new AnnotationConfigApplicationContext(
            ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
    EventMain consumer = context.getBean(EventMain.class);
    Config config = ConfigFactory.load();
    String bootstrapServers = config.getString("bootstrap.servers");
    log.info("Bootstrap servers are: {}", bootstrapServers);
    String groupId = config.getString("group.id");
    final ConsumerSettings<String, String> defaultConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(groupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    groupId = config.getString("consignmentblocker.group.id");
    log.info("group id for consignment blocker consumer {}", groupId);
    final ConsumerSettings<String, String> consignmentBlockerConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(groupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    consumer.load(materializer, defaultConsumerSettings, consignmentBlockerConsumerSettings);
    String bfPickupChargesGroupId = config.getString("bfPickupCharges.group.id");
    final ConsumerSettings<String, String> bfPickupChargesConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(bfPickupChargesGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    consumer.loadBfPickupCharges(materializer, bfPickupChargesConsumerSettings);
  }

  public void load(
      ActorMaterializer materializer,
      ConsumerSettings<String, String> consumerSettings,
      ConsumerSettings<String, String> consignmentBlockerConsumerSettings) {
    log.info("Loading zoom event trigger consumer with settings {}", consumerSettings);
    zoomEventTriggerConsumer.load(materializer, consumerSettings);
    log.info(
        "Loading consignment blocker consumer with settings {}",
        consignmentBlockerConsumerSettings);
    consignmentBlockUnblockConsumer.load(materializer, consignmentBlockerConsumerSettings);
  }

  public void loadBfPickupCharges(
      ActorMaterializer materializer, ConsumerSettings<String, String> consumerSettings) {
    bfPickupChargesActionConsumer.load(materializer, consumerSettings);
  }
}
