package consumer.depsNotification;

import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsumerMessages;
import com.rivigo.zoom.common.repository.mysql.ConsumerMessagesRepository;
import enums.ProducerTopics;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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



}
