package consumer.depsNotification;

import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.ConsumerMessages;
import com.rivigo.zoom.common.repository.mysql.ConsumerMessagesRepository;
import enums.ProducerTopics;
import lombok.Getter;
import lombok.Setter;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by ashfakh on 9/10/17.
 */

@Getter
@Setter
@Component
public class ConsumerTimer implements TimerTask {

    @Autowired
    ConsumerMessagesRepository consumerMessagesRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private Long msgId;

    @Override
    public void run(Timeout timeout) throws Exception{
        kafkaTemplate.send("COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR",msgId);
    }

}
