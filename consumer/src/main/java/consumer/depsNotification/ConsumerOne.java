package consumer.depsNotification;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import com.rivigo.zoom.common.enums.Topic;
import config.ServiceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
public class ConsumerOne extends ConsumerModel {

    public String processMessage(String str){
        return str;
    }



}
