package com.rivigo.riconet.notification.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.AsyncConfig;
import com.rivigo.riconet.core.config.KafkaConfig;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.core.consumer.HealthCheckConsumer;
import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.notification.consumer.AppointmentNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DEPSNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DocIssueNotificationConsumer;
import com.rivigo.riconet.notification.consumer.PickupNotificationConsumer;
import com.rivigo.riconet.notification.consumer.RetailNotificationConsumer;
import com.rivigo.riconet.notification.consumer.ZoomCommunicationsConsumer;
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
import org.springframework.stereotype.Component;

/** Created by ashfakh on 9/10/17. */
@Component
@Slf4j
public class NotificationMain {

  private final DEPSNotificationConsumer depsNotificationConsumer;

  private final DocIssueNotificationConsumer docIssueNotificationConsumer;

  private final PickupNotificationConsumer pickupNotificationConsumer;

  private final AppointmentNotificationConsumer appointmentNotificationConsumer;

  private final RetailNotificationConsumer retailNotificationConsumer;

  private final ZoomCommunicationsConsumer zoomCommunicationsConsumer;

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

  @Value("${notification.healthCheckConsumer.group.id}")
  private String healthCheckConsumerGroup;

  @Value("${notification.group.id}")
  private String notificationConsumerGroup;

  public NotificationMain(
      HealthCheckConsumer healthCheckConsumer,
      DEPSNotificationConsumer depsNotificationConsumer,
      DocIssueNotificationConsumer docIssueNotificationConsumer,
      PickupNotificationConsumer pickupNotificationConsumer,
      AppointmentNotificationConsumer appointmentNotificationConsumer,
      RetailNotificationConsumer retailNotificationConsumer,
      ZoomCommunicationsConsumer zoomCommunicationsConsumer) {
    this.healthCheckConsumer = healthCheckConsumer;
    this.depsNotificationConsumer = depsNotificationConsumer;
    this.docIssueNotificationConsumer = docIssueNotificationConsumer;
    this.pickupNotificationConsumer = pickupNotificationConsumer;
    this.appointmentNotificationConsumer = appointmentNotificationConsumer;
    this.retailNotificationConsumer = retailNotificationConsumer;
    this.zoomCommunicationsConsumer = zoomCommunicationsConsumer;
  }

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("notifications");
    ApplicationContext context =
        new AnnotationConfigApplicationContext(
            ServiceConfig.class,
            ZoomConfig.class,
            TracingConfig.class,
            ZoomDatabaseConfig.class,
            AsyncConfig.class,
            KafkaConfig.class);
    final ActorMaterializer materializer = ActorMaterializer.create(system);

    NotificationMain notificationMain = context.getBean(NotificationMain.class);
    notificationMain.initialize(materializer, system);
  }

  private void initialize(ActorMaterializer materializer, ActorSystem system) {
    log.info("Bootstrap servers are: {}", bootstrapServers);
    load(materializer, system, bootstrapServers, healthCheckConsumerGroup, healthCheckConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        depsNotificationConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        docIssueNotificationConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        pickupNotificationConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        appointmentNotificationConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        retailNotificationConsumer);
    load(
        materializer,
        system,
        bootstrapServers,
        notificationConsumerGroup,
        zoomCommunicationsConsumer);
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
