package depsNotification;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
@Slf4j
public class DEPSNotificationMain {

    @Autowired
    private DEPSNotificationConsumer depsNotificationConsumer;

    public static void main(String args[]){
        final ActorSystem system = ActorSystem.create("notifications");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        DEPSNotificationMain consumer=context.getBean(DEPSNotificationMain.class);
        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer.load(system,materializer,consumerSettings);
    }

    public void load(ActorSystem system, ActorMaterializer materializer,ConsumerSettings<String, String> consumerSettings){
        depsNotificationConsumer.load(system,materializer,consumerSettings);

    }
}
