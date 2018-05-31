package com.rivigo.riconet.core.service;

import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface DEPSRecordService {

  List<DEPSNotification> createNotificationData(DEPSNotificationContext depsNotificationContext);

  void sendNotifications(List<DEPSNotification> depsNotificationList);

  DEPSNotificationContext getNotificationContext(List<DEPSNotificationDTO> depsRecordList);
}
