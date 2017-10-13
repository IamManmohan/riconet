package depsNotification;

import ConsumerAbstract.ConsumerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;

import java.util.List;

@Slf4j
@Component
public class DEPSNotificationConsumer extends ConsumerModel {


  @Autowired
  private DEPSRecordService depsRecordService;

  public String processMessage(String str){
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    DEPSNotificationContext context = null;
    try {
      TypeReference<List<DEPSNotificationDTO>> mapType = new TypeReference<List<DEPSNotificationDTO>>() {};
      List<DEPSNotificationDTO> depsRecordList = objectMapper.readValue(str, mapType);
      context = depsRecordService.getNotificationContext(depsRecordList);
    }catch (Exception e){
      e.printStackTrace();
    }
    List<DEPSNotification> depsNotificationList = depsRecordService.createNotificationData(context);
    depsRecordService.sendNotifications(depsNotificationList);
    return str;
  }

  public DEPSNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name());
  }
}
