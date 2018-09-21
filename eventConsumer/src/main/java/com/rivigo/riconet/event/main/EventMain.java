package com.rivigo.riconet.event.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.event.consumer.BfPickupChargesActionConsumer;
import com.rivigo.riconet.event.consumer.CnActionConsumer;
import com.rivigo.riconet.event.consumer.ConsignmentBlockUnblockConsumer;
import com.rivigo.riconet.event.consumer.FinanceEventsConsumer;
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

  private final FinanceEventsConsumer financeEventsConsumer;

  private static final String LATEST = "latest";

  private final CnActionConsumer cnActionConsumer;

  private static final String CONSUMER_OFFSET_CONFIG = "latest";

  public EventMain(
      ZoomEventTriggerConsumer zoomEventTriggerConsumer,
      ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer,
      BfPickupChargesActionConsumer bfPickupChargesActionConsumer,
      FinanceEventsConsumer financeEventsConsumer,
      CnActionConsumer cnActionConsumer) {
    this.zoomEventTriggerConsumer = zoomEventTriggerConsumer;
    this.consignmentBlockUnblockConsumer = consignmentBlockUnblockConsumer;
    this.bfPickupChargesActionConsumer = bfPickupChargesActionConsumer;
    this.financeEventsConsumer = financeEventsConsumer;
    this.cnActionConsumer = cnActionConsumer;
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

    String zoomEventTriggerConsumerGroupId = config.getString("zoomEventTriggerConsumer.group.id");
    final ConsumerSettings<String, String> zoomEventTriggerConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(zoomEventTriggerConsumerGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);

    String cnActionConsumerGroupId = config.getString("cnActionConsumer.group.id");
    final ConsumerSettings<String, String> cnActionConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(cnActionConsumerGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);

    String consignmentblockerGroupId = config.getString("consignmentblocker.group.id");
    final ConsumerSettings<String, String> consignmentBlockerConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(consignmentblockerGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);

    String financeEventsConsumerGroupId = config.getString("financeEventsConsumer.group.id");
    final ConsumerSettings<String, String> financeEventsConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(financeEventsConsumerGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);

    String bfPickupChargesGroupId = config.getString("bfPickupCharges.group.id");
    final ConsumerSettings<String, String> bfPickupChargesConsumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(bfPickupChargesGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);

    consumer.load(
        materializer,
        zoomEventTriggerConsumerSettings,
        cnActionConsumerSettings,
        consignmentBlockerConsumerSettings,
        financeEventsConsumerSettings,
        bfPickupChargesConsumerSettings);
  }

  private void load(
      ActorMaterializer materializer,
      ConsumerSettings<String, String> zoomEventTriggerConsumerSettings,
      ConsumerSettings<String, String> cnActionConsumerSettings,
      ConsumerSettings<String, String> consignmentBlockerConsumerSettings,
      ConsumerSettings<String, String> financeEventsConsumerSettings,
      ConsumerSettings<String, String> bfPickupChargesActionConsumerSettings) {
    log.info(
        "Loading zoom event trigger consumer with settings {}", zoomEventTriggerConsumerSettings);
    zoomEventTriggerConsumer.load(materializer, zoomEventTriggerConsumerSettings);
    log.info(
        "Loading event trigger for cn status change with settings {}", cnActionConsumerSettings);
    cnActionConsumer.load(materializer, cnActionConsumerSettings);
    log.info(
        "Loading consignment blocker consumer with settings {}",
        consignmentBlockerConsumerSettings);
    consignmentBlockUnblockConsumer.load(materializer, consignmentBlockerConsumerSettings);
    log.info("Loading Finance event consumer with settings {}", financeEventsConsumerSettings);
    financeEventsConsumer.load(materializer, financeEventsConsumerSettings);
    log.info(
        "Loading bfPickup event consumer with settings {}", bfPickupChargesActionConsumerSettings);
    bfPickupChargesActionConsumer.load(materializer, bfPickupChargesActionConsumerSettings);
  }
}
