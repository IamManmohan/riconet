package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.dto.LocationBasicDTO;
import com.rivigo.zoom.common.dto.UserBasicDTO;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNotification extends AbstractMailNotificationEntity {

  private String id;
  private String cnote;
  private Long consignmentId;
  private UserBasicDTO responsiblePerson;
  private LocationBasicDTO responsibleLocation;
  private Set<String> emailIdList = new HashSet<>();
  private Set<String> ccList = new HashSet<>();
  private Set<String> bccList = new HashSet<>();
}
