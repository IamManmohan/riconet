package com.rivigo.riconet.event.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.AsyncConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.event.consumer.BfPickupChargesActionConsumer;
import com.rivigo.riconet.event.consumer.CnActionConsumer;
import com.rivigo.riconet.event.consumer.ConsignmentBlockUnblockConsumer;
import com.rivigo.riconet.event.consumer.FinanceEventsConsumer;
import com.rivigo.riconet.event.consumer.WmsEventConsumer;
import com.rivigo.riconet.event.consumer.ZoomEventTriggerConsumer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
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

  private final CnActionConsumer cnActionConsumer;

  private final WmsEventConsumer wmsEventConsumer;

  private static final String CONSUMER_OFFSET_CONFIG = "latest";

  @Value("${bootstrap.servers}")
  private String bootstrapServers;

  @Value("${zoomEventTriggerConsumer.group.id}")
  private String zoomEventTriggerConsumerGroupId;

  @Value("${cnActionConsumer.group.id}")
  private String cnActionConsumerGroupId;

  @Value("${consignmentblocker.group.id}")
  private String consignmentBlockerGroupId;

  @Value("${financeEventsConsumer.group.id}")
  private String financeEventsConsumerGroupId;

  @Value("${bfPickupCharges.group.id}")
  private String bfPickupChargesActionConsumerGroupId;

  @Value("${wmsEventConsumer.group.id}")
  private String wmsEventConsumerGroupId;

  public EventMain(
      ZoomEventTriggerConsumer zoomEventTriggerConsumer,
      ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer,
      BfPickupChargesActionConsumer bfPickupChargesActionConsumer,
      FinanceEventsConsumer financeEventsConsumer,
      CnActionConsumer cnActionConsumer,
      WmsEventConsumer wmsEventConsumer) {
    this.zoomEventTriggerConsumer = zoomEventTriggerConsumer;
    this.consignmentBlockUnblockConsumer = consignmentBlockUnblockConsumer;
    this.bfPickupChargesActionConsumer = bfPickupChargesActionConsumer;
    this.financeEventsConsumer = financeEventsConsumer;
    this.cnActionConsumer = cnActionConsumer;
    this.wmsEventConsumer = wmsEventConsumer;
  }

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("events");
    ApplicationContext context =
        new AnnotationConfigApplicationContext(
            ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class, AsyncConfig.class);
    final ActorMaterializer materializer = ActorMaterializer.create(system);
    EventMain eventMain = context.getBean(EventMain.class);
    eventMain.initialize(materializer, system);
  }

  private void initialize(ActorMaterializer materializer, ActorSystem system) {
    log.info("Bootstrap servers are: {}", bootstrapServers);
    load(materializer, system, zoomEventTriggerConsumerGroupId, zoomEventTriggerConsumer);
    load(materializer, system, cnActionConsumerGroupId, cnActionConsumer);
    load(materializer, system, consignmentBlockerGroupId, consignmentBlockUnblockConsumer);
    load(materializer, system, financeEventsConsumerGroupId, financeEventsConsumer);
    load(materializer, system, bfPickupChargesActionConsumerGroupId, bfPickupChargesActionConsumer);
    load(materializer, system, wmsEventConsumerGroupId, wmsEventConsumer);
  }

  private void load(
      ActorMaterializer materializer,
      ActorSystem system,
      String consumerGroupId,
      ConsumerModel consumer) {
    final ConsumerSettings<String, String> consumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(consumerGroupId)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG);
    log.info("Loading zoom event trigger consumer with settings {}", consumerSettings.toString());
    consumer.load(materializer, consumerSettings);
  }
}
