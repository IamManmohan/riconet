package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.model.Pickup;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface PickupService {

  Map<Long, Pickup> getPickupMapByIdIn(List<Long> pickupTripIdList);

  List<Pickup> getPickups(List<Long> pickupIds);

  void processPickupNotificationDTOList(List<PickupNotificationDTO> pickupNotificationDTOList);

  void deductPickupCharges(NotificationDTO notificationDTO);
}
