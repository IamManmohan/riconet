package consumer.depsNotification;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.ConsumerMessages;
import com.rivigo.zoom.common.repository.mysql.ConsumerMessagesRepository;
import enums.ProducerTopics;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ashfakh on 26/9/17.
 */


@Slf4j
@Getter
@Setter
@Component
public abstract class ConsumerModel {

    private final String topic;

    private final String errorTopic;

    @Autowired
    private DEPSRecordService depsRecordService;

    @Autowired
    ExecutorService executorService;

    @Autowired
    ConsumerMessagesRepository consumerMessagesRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private Timer timer=new HashedWheelTimer();

    private final AtomicLong offset = new AtomicLong();

    public ConsumerModel(String topic, String errorTopic){
        this.topic=topic;
        this.errorTopic=errorTopic;
    }

    @Async
    private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
        if(record.topic().toString().equals(ProducerTopics.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.toString())){
            log.info("");
            executorService.submit(()->{
                try {
                    processMessage(record.value());
                }catch (Exception e){
                    processError(record.value());
                    e.printStackTrace();
                }
            });
        }
//        else if(record.topic().toString().equals("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR")){
//            executorService.submit(()->{
//                ConsumerMessages consumerMessages=consumerMessagesRepository.findById(Long.valueOf(record.value()));
//                try {
//                    processMessage(consumerMessages.getMessage());
//                }catch (Exception e){
//                    processError(consumerMessages.getMessage());
//                    e.printStackTrace();
//                }
//            });
//        }
        offset.set(record.offset());
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Long> loadOffset() {
        return CompletableFuture.completedFuture(offset.get());
    }

    public abstract String processMessage(String str);

    String processError(String str){
        System.out.print("processing error");
        ConsumerMessages consumerMessage;
        consumerMessage = consumerMessagesRepository.findByMessage(str);

        if(consumerMessage==null){
            consumerMessage=new ConsumerMessages();
            consumerMessage.setMessage(str);
            consumerMessage.setRetry_count(1L);
            consumerMessage.setRetry_time(DateTime.now().getMillis());
            consumerMessage.setTopic(ProducerTopics.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.toString());
        }else{
            consumerMessage.setRetry_count(consumerMessage.getRetry_count()+1L);
        }
        consumerMessage=consumerMessagesRepository.save(consumerMessage);
        if(consumerMessage.getRetry_count()<6L) {
            ConsumerTimer task = new ConsumerTimer(consumerMessage.getId(),errorTopic);
            timer.newTimeout(task, 30, TimeUnit.SECONDS);
        }
        return str;
    }


    public void load(ActorSystem system, ActorMaterializer materializer,ConsumerSettings<String, String> consumerSettings) {
        Set<String> topics=new HashSet<>();
        topics.add(topic);
        topics.add(errorTopic);


        this.loadOffset()
                .thenAccept(fromOffset -> Consumer
                        .plainSource(
                                consumerSettings,
                                Subscriptions.topics(topics)
                        )
                        .mapAsync(1, this::save)
                        .runWith(Sink.ignore(), materializer));
    }
}


