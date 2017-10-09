package consumer.depsNotification;

import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.Consignment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
public class ConsumerOne extends ConsumerModel {

    public String processMessage(String str){
        if(str.equals("hai")){
            System.out.println("--------------------Failure---------------");
            Consignment cn=null;
            str=cn.getCnote();
            System.out.println("--------------------Failure-sssss--------------");
        }
        System.out.println("--------------------SUCCESS---------------"+str+"------");
        return str;
    }

    public ConsumerOne(){
        super(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name());
    }
}
