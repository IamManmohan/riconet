package ConsumerAbstract;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created by ashfakh on 9/10/17.
 */

public class ConsumerTimer implements TimerTask {

    private final String msgId;

    private final String topic;

    private KafkaTemplate kafkaTemplate;

    public ConsumerTimer(String msgId,String topic,KafkaTemplate kafkaTemplate){
        this.msgId=msgId;
        this.topic=topic;
        this.kafkaTemplate=kafkaTemplate;
    }


    @Override
    public void run(Timeout timeout) throws Exception{
        kafkaTemplate.send(topic,msgId);
    }

}
