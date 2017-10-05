package consumer.depsNotification;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.rivigo.zoom.common.config.ZoomConfig;
import com.rivigo.zoom.common.config.ZoomDatabaseConfig;
import config.ServiceConfig;
import dto.TestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ashfakh on 26/9/17.
 */


@Slf4j
@Component
public class ConsumerModel {

    @Autowired
    private DEPSRecordService depsRecordService;

    //@Autowired
    //private KafkaTemplate kafkaTemplate;

    private final AtomicLong offset = new AtomicLong();

    @Async
    private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
        System.out.println("DB.save: " + record.value());
        String st=processMeassage(record.value());
        //System.out.print(st);
        offset.set(record.offset());
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    public CompletionStage<Done> process(TestDTO data) {
        System.out.println("Rocket launched to " + data);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Long> loadOffset() {
        return CompletableFuture.completedFuture(offset.get());
        }

    private String processMeassage(String str){
        if(str.equals("hai")){
            return str;
        }
        return null;
    }


    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("kafka-consumer-depsNotification");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ApplicationContext context= new AnnotationConfigApplicationContext(ServiceConfig.class, ZoomConfig.class, ZoomDatabaseConfig.class);
        ConsumerModel consumerModel=context.getBean(ConsumerModel.class);



        final ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


        Set<String> topicSets = new HashSet<>();
        topicSets.add("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION");

//    depsNotification
//      .loadOffset()
//      .thenAccept(fromOffset -> Consumer
//        .plainSource(
//          consumerSettings,
//          Subscriptions.topics(topicSets)
//        )
//        .mapAsync(1, depsNotification::save)
//        .runWith(Sink.ignore(), materializer));

        consumerModel
                .loadOffset()
                .thenAccept(fromOffset -> Consumer
                        .plainSource(
                                consumerSettings,
                                Subscriptions.assignmentWithOffset(new TopicPartition("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION", 0), fromOffset)
                        )
                        .mapAsync(1, consumerModel::save)
                        .runWith(Sink.ignore(), materializer));
    }
}


