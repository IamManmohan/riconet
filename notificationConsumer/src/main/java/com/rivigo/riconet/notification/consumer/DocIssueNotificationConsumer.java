package com.rivigo.riconet.notification.consumer;

import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.DocumentIssueNotificationService;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.common.model.mongo.DocumentIssueNotification;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DocIssueNotificationConsumer extends ConsumerModel {

  @Autowired private DocumentIssueNotificationService documentIssueNotificationService;

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_DOCUMENT_ISSUE_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_DOCUMENT_ISSUE_NOTIFICATION_ERROR.name();
  }

  public void processMessage(String str) {
    String[] split = str.split("\\|");
    if (split.length != 4) {
      throw new ZoomException("Error in message format");
    }
    Long consignmentId = Long.parseLong(split[0]);
    Long userId = Long.parseLong(split[1]);
    String subReason = split[2];
    ConsignmentStatus status = ConsignmentStatus.valueOf(split[3]);
    DocumentIssueNotification notification =
        documentIssueNotificationService.createNotificationData(
            consignmentId, userId, subReason, status);
    if (notification != null) {
      documentIssueNotificationService.sendNotifications(notification);
    }
  }
}
