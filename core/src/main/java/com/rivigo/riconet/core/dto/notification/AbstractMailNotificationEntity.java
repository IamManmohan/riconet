package com.rivigo.riconet.core.dto.notification;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractMailNotificationEntity {
  private Set<String> emailIdList = new HashSet<>();
  private Set<String> ccList = new HashSet<>();
  private Set<String> bccList = new HashSet<>();
}
