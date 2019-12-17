package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.dto.LocationBasicDTO;
import com.rivigo.zoom.common.dto.UserBasicDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AppointmentNotification extends AbstractMailNotificationEntity {
  private String id;
  private String cnote;
  private Long consignmentId;
  private UserBasicDTO responsiblePerson;
  private LocationBasicDTO responsibleLocation;
}
