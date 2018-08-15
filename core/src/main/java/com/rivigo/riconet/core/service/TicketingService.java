package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
public interface TicketingService {

  void sendTicketCreationEmail(NotificationDTO notificationDTO);

  void sendTicketAssigneeChangeEmail(NotificationDTO notificationDTO);

  void sendTicketStatusChangeEmail(NotificationDTO notificationDTO);

  void sendTicketEscalationChangeEmail(NotificationDTO notificationDTO);

  void sendTicketCcNewPersonAdditionEmail(NotificationDTO notificationDTO);

  void sendTicketSeverityChangeEmail(NotificationDTO notificationDTO);

  void sendTicketCommentCreationEmail(NotificationDTO notificationDTO);
}
