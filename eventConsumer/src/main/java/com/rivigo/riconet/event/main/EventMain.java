package com.rivigo.riconet.event.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.AsyncConfig;
import com.rivigo.riconet.core.config.KafkaConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.consumer.HealthCheckConsumer;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.event.consumer.BfPickupChargesActionConsumer;
import com.rivigo.riconet.event.consumer.CnActionConsumer;
import com.rivigo.riconet.event.consumer.ConsignmentBlockUnblockConsumer;
import com.rivigo.riconet.event.consumer.ExpressAppPickupConsumer;
import com.rivigo.riconet.event.consumer.FinanceEventsConsumer;
import com.rivigo.riconet.event.consumer.KairosExpressAppEventConsumer;
import com.rivigo.riconet.event.consumer.VendorOnboardingEventConsumer;
import com.rivigo.riconet.event.consumer.WmsEventConsumer;
import com.rivigo.riconet.event.consumer.ZoomEventTriggerConsumer;
import com.rivigo.zoom.common.config.TracingConfig;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/** Created by ashfakh on 12/05/18. */
@Component
@Slf4j
@Configuration
public class EventMain {

  private final ZoomEventTriggerConsumer zoomEventTriggerConsumer;

  private final ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer;

  private final BfPickupChargesActionConsumer bfPickupChargesActionConsumer;

  private final FinanceEventsConsumer financeEventsConsumer;

  private final CnActionConsumer cnActionConsumer;

  private final WmsEventConsumer wmsEventConsumer;

  private final KairosExpressAppEventConsumer kairosExpressAppEventConsumer;

  private final ExpressAppPickupConsumer expressAppPickupConsumer;

  private final VendorOnboardingEventConsumer vendorOnboardingEventConsumer;

  private final HealthCheckConsumer healthCheckConsumer;

  private static final String CONSUMER_OFFSET_CONFIG = "latest";

  @Value("${bootstrap.servers}")
  private String bootstrapServers;

  @Value("${akka.kafka.consumer.poll-interval-ms}")
  private Long pollIntervalMillis;

  @Value("${akka.kafka.consumer.poll-timeout-ms}")
  private Long pollTimeOutMillis;

  @Value("${akka.kafka.consumer.stop-timeout-ms}")
  private Long stopTimeoutMillis;

  @Value("${akka.kafka.consumer.commit-timeout-ms}")
  private Long commitTimeoutMillis;

  @Value("${akka.kafka.consumer.kafka-clients.enable.auto.commit}")
  private String autoCommitEnabled;

  @Value("${event.healthCheckConsumer.group.id}")
  private String healthCheckConsumerGroup;

  @Value("${zoomEventTriggerConsumer.group.id}")
  private String zoomEventTriggerGroup;

  @Value("${cnActionConsumer.group.id}")
  private String cnActionGroup;

  @Value("${consignmentblocker.group.id}")
  private String consignmentBlockerGroup;

  @Value("${financeEventsConsumer.group.id}")
  private String financeEventsGroup;

  @Value("${bfPickupCharges.group.id}")
  private String bfPickupChargesGroup;

  @Value("${wmsEventConsumer.group.id}")
  private String wmsEventGroup;

  @Value("${kairosExpressAppEventConsumer.group.id}")
  private String kairosExpressAppGroup;

  @Value("${expressAppPickupConsumer.group.id}")
  private String expressAppPickupGroup;

  @Value("${vendorOnboardingEventConsumer.group.id}")
  private String vendorOnboardingEventGroup;

  public EventMain(
      HealthCheckConsumer healthCheckConsumer,
      ZoomEventTriggerConsumer zoomEventTriggerConsumer,
      ConsignmentBlockUnblockConsumer consignmentBlockUnblockConsumer,
      BfPickupChargesActionConsumer bfPickupChargesActionConsumer,
      FinanceEventsConsumer financeEventsConsumer,
      CnActionConsumer cnActionConsumer,
      WmsEventConsumer wmsEventConsumer,
      KairosExpressAppEventConsumer kairosExpressAppEventConsumer,
      ExpressAppPickupConsumer expressAppPickupConsumer,
      VendorOnboardingEventConsumer vendorOnboardingEventConsumer) {
    this.healthCheckConsumer = healthCheckConsumer;
    this.zoomEventTriggerConsumer = zoomEventTriggerConsumer;
    this.consignmentBlockUnblockConsumer = consignmentBlockUnblockConsumer;
    this.bfPickupChargesActionConsumer = bfPickupChargesActionConsumer;
    this.financeEventsConsumer = financeEventsConsumer;
    this.cnActionConsumer = cnActionConsumer;
    this.wmsEventConsumer = wmsEventConsumer;
    this.kairosExpressAppEventConsumer = kairosExpressAppEventConsumer;
    this.expressAppPickupConsumer = expressAppPickupConsumer;
    this.vendorOnboardingEventConsumer = vendorOnboardingEventConsumer;
  }

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("events");
    ApplicationContext context =
        new AnnotationConfigApplicationContext(
            ServiceConfig.class,
            ZoomConfig.class,
            TracingConfig.class,
            ZoomDatabaseConfig.class,
            AsyncConfig.class,
            KafkaConfig.class);
    final ActorMaterializer materializer = ActorMaterializer.create(system);
    EventMain eventMain = context.getBean(EventMain.class);
    eventMain.initialize(materializer, system);
    log.info("Loaded all consumers with respective settings");
  }

  private void initialize(ActorMaterializer materializer, ActorSystem system) {
    //    String bootstrapServers = config.getString("bootstrap.servers");
    log.info("Bootstrap servers are: {}", bootstrapServers);
    load(materializer, system, bootstrapServers, healthCheckConsumerGroup, healthCheckConsumer);
    load(materializer, system, bootstrapServers, zoomEventTriggerGroup, zoomEventTriggerConsumer);
    load(materializer, system, bootstrapServers, cnActionGroup, cnActionConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        consignmentBlockerGroup,
        consignmentBlockUnblockConsumer);
    load(materializer, system, bootstrapServers, financeEventsGroup, financeEventsConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        bfPickupChargesGroup,
        bfPickupChargesActionConsumer);
    load(materializer, system, bootstrapServers, wmsEventGroup, wmsEventConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        kairosExpressAppGroup,
        kairosExpressAppEventConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        vendorOnboardingEventGroup,
        vendorOnboardingEventConsumer);
  }

  private void load(
      ActorMaterializer materializer,
      ActorSystem system,
      String bootstrapServers,
      String consumerGroupId,
      ConsumerModel consumer) {
    final ConsumerSettings<String, String> consumerSettings =
        ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
            .withBootstrapServers(bootstrapServers)
            .withGroupId(consumerGroupId)
            .withPollInterval(Duration.ofMillis(pollIntervalMillis))
            .withPollTimeout(Duration.ofMillis(pollTimeOutMillis))
            .withStopTimeout(Duration.ofMillis(stopTimeoutMillis))
            .withCommitTimeout(Duration.ofMillis(commitTimeoutMillis))
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_CONFIG)
            .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommitEnabled);
    log.info("Loading consumer with settings {}", consumerSettings.toString());
    consumer.load(materializer, consumerSettings);
  }
}
