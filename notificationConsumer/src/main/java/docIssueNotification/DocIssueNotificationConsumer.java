package docIssueNotification;

import ConsumerAbstract.ConsumerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.model.mongo.DocumentIssueNotification;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.DEPSRecordService;
import service.DocumentIssueNotificationService;

import java.util.List;

@Slf4j
@Component
public class DocIssueNotificationConsumer extends ConsumerModel {


  @Autowired
  private DocumentIssueNotificationService documentIssueNotificationService;

  public String processMessage(String str){
    String[] split=str.split("\\|");
    if(split.length !=4){
      throw  new ZoomException("Error in message format");
    }
    Long consignmentId=Long.parseLong(split[0]);
    Long userId=Long.parseLong(split[1]);
    String subReason=split[2];
    ConsignmentStatus status=ConsignmentStatus.valueOf(split[3]);
    DocumentIssueNotification notification=documentIssueNotificationService.createNotificationData(consignmentId,userId,subReason,status);
    documentIssueNotificationService.sendNotifications(notification);
    return str;
  }

  public DocIssueNotificationConsumer(){
    super(Topic.COM_RIVIGO_ZOOM_DOCUMENT_ISSUE_NOTIFICATION.name(),Topic.COM_RIVIGO_ZOOM_DOCUMENT_ISSUE_NOTIFICATION_ERROR.name());
  }
}
