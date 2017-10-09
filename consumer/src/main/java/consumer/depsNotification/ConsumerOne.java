package consumer.depsNotification;

import com.rivigo.zoom.common.enums.Topic;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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

    public ConsumerOne(){
        super(new HashSet<>(Arrays.asList(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name())),
                new HashSet<>(Arrays.asList(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name())));
    }
}
