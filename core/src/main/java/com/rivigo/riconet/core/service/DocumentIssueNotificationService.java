package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.notification.DocumentIssueNotification;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface DocumentIssueNotificationService {

  DocumentIssueNotification createNotificationData(
      Long consignmentId, Long userId, String subReason, ConsignmentStatus status);

  ConsignmentSchedule getPreviousSchedule(List<ConsignmentSchedule> consignmentSchedules);

  ConsignmentSchedule getCurrentSchedule(List<ConsignmentSchedule> consignmentSchedules);

  void sendNotifications(DocumentIssueNotification notification);
}
