package com.rivigo.riconet.notification.main;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.riconet.core.config.ServiceConfig;
import com.rivigo.riconet.notification.consumer.AppointmentNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DEPSNotificationConsumer;
import com.rivigo.riconet.notification.consumer.DocIssueNotificationConsumer;
import com.rivigo.riconet.notification.consumer.PickupNotificationConsumer;
import com.rivigo.riconet.notification.consumer.RetailNotificationConsumer;
import com.rivigo.riconet.notification.consumer.ZoomCommunicationsConsumer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
@Slf4j
public class NotificationMain {

    @Value("bootstrap.servers")
    private static String bootstrapServers;

    @Value("group.id")
    private static String groupId;

    @Autowired
    private DEPSNotificationConsumer depsNotificationConsumer;

    @Autowired
    private DocIssueNotificationConsumer docIssueNotificationConsumer;

    @Autowired
    private PickupNotificationConsumer pickupNotificationConsumer;

    @Autowired
    private AppointmentNotificationConsumer appointmentNotificationConsumer;

    @Autowired
    private RetailNotificationConsumer retailNotificationConsumer;

    @Autowired
    private ZoomCommunicationsConsumer zoomCommunicationsConsumer;

    public static void main(String[] args){
        final ActorSystem system = ActorSystem.create("notifications");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        NotificationMain consumer=context.getBean(NotificationMain.class);
        Config config= ConfigFactory.load();
        bootstrapServers=config.getString("bootstrap.servers");
        log.info("Bootstrap servers are:");
        log.info(bootstrapServers);
        bootstrapServers = "ts-qa-kafka-1.rivigo.com:9092";
        groupId=config.getString("group.id");
        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers(bootstrapServers)
                        .withGroupId(groupId)
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer.load(materializer,consumerSettings);
    }

    public void load(ActorMaterializer materializer,ConsumerSettings<String, String> consumerSettings){
        depsNotificationConsumer.load(materializer,consumerSettings);
        docIssueNotificationConsumer.load(materializer,consumerSettings);
        pickupNotificationConsumer.load(materializer,consumerSettings);
        appointmentNotificationConsumer.load(materializer,consumerSettings);
        retailNotificationConsumer.load(materializer,consumerSettings);
    }
}
