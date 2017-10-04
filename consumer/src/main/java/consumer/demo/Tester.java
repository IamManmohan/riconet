package consumer.demo;

import com.rivigo.zoom.common.enums.Topic;
import dto.BarcodeScanDTO;
import dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created by ashfakh on 26/9/17.
 */
public class Tester {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void main(String[] args) {
        TestDTO dto=new TestDTO();
        dto.setMessage("haidjsoifd");
        dto.setRetry_count(0L);
        kafkaTemplate.send(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.toString(),dto);
    }
}
