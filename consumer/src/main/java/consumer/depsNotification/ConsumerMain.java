package consumer.depsNotification;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.rivigo.zoom.common.enums.Topic;
import config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
public class ConsumerMain {

//    @Autowired
//    private static ConsumerOne consumerOne;

    public static void main(String args[]){
        final ActorSystem system = ActorSystem.create("kafka-consumer-depsNotification");
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        ConsumerOne consumer=context.getBean(ConsumerOne.class);
        Set<Topic> topics=new HashSet<>();
        topics.add(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION);
        consumer.load(system,materializer,topics);


    }
}
