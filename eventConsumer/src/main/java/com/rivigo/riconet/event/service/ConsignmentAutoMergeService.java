package com.rivigo.riconet.event.service;

import com.rivigo.riconet.core.dto.NotificationDTO;

public interface ConsignmentAutoMergeService {

    void autoMergeSecondaryConsignment(NotificationDTO notificationDTO);
}
