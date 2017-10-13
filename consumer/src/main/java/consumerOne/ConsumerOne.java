package consumerOne;

import ConsumerAbstract.ConsumerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.IndustryType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashfakh on 9/10/17.
 */
@Component
public class ConsumerOne extends ConsumerModel{

    public String processMessage(String str){
        if(str.equals("hai")){
            System.out.println("--------------------Failure---------------");
            Consignment cn=null;
            str=cn.getCnote();
            System.out.println("--------------------Failure-sssss--------------");
        }
        System.out.println("--------------------SUCCESS---------------"+str+"------");
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        TypeReference<List<IndustryType>> mapType = new TypeReference<List<IndustryType>>() {};
//        List<IndustryType> industryTypes=new ArrayList<>();
//        try {
//            industryTypes = objectMapper.readValue(str, mapType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for(int i=0;i<industryTypes.size();i++){
//            System.out.println(industryTypes.get(i).getContentTypes()+"--- Content Types");
//            System.out.println(industryTypes.get(i).getType()+"---- Types");
//        }
        return str;
    }

    public ConsumerOne(){
        super(Topic.TEST.name(),Topic.TEST_ERROR.name());
    }
}
