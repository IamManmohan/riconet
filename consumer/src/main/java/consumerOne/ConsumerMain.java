package consumerOne;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.stream.ActorMaterializer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import config.ServiceConfig;
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
public class ConsumerMain {

    @Value("bootstrap.servers")
    private static String bootstrapServers;

    @Autowired
    private ConsumerOne consumerOne;

    public static void main(String args[]){
        final ActorSystem system = ActorSystem.create("notifications");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        ConsumerMain consumer=context.getBean(ConsumerMain.class);
        Config config= ConfigFactory.load();
        bootstrapServers=config.getString("bootstrap.servers");
        System.out.println("------------"+bootstrapServers+"----------------");
        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers(bootstrapServers)
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer.load(system,materializer,consumerSettings);
    }


    public void load(ActorSystem system, ActorMaterializer materializer,ConsumerSettings<String, String> consumerSettings){
        consumerOne.load(system,materializer,consumerSettings);

    }
}
