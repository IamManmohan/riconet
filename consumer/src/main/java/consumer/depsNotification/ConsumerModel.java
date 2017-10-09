package consumer.depsNotification;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import com.rivigo.zoom.common.enums.Topic;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ashfakh on 26/9/17.
 */


@Slf4j
@Getter
@Setter
@Component
public abstract class ConsumerModel {

    private final Set<String> topic;

    private final Set<String> errorTopic;

    public ConsumerModel(Set<String> topic, Set<String> errorTopic){
        this.topic=topic;
        this.errorTopic=errorTopic;
    }

    @Autowired
    private DEPSRecordService depsRecordService;

    @Autowired
    ExecutorService executorService;

    private Timer timer=new HashedWheelTimer();

    private final AtomicLong offset = new AtomicLong();

    @Async
    private CompletionStage<Done> save(ConsumerRecord<String, String> record) {
        log.info("");
        executorService.submit(()->{
            processMessage(record.value());
        });
        offset.set(record.offset());
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private CompletionStage<Long> loadOffset() {
        return CompletableFuture.completedFuture(offset.get());
    }

    public abstract String processMessage(String str);

    public void load(ActorSystem system, ActorMaterializer materializer,ConsumerSettings<String, String> consumerSettings) {
        Set<String> topics=new HashSet<>();
        topics.addAll(topic);
        topics.addAll(errorTopic);

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


