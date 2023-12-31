package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Created by aditya on 23/2/18. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ZoomCommunicationsDTO {

  private Boolean confidential = Boolean.TRUE;

  private String message;
  private String templateV2;
  private String phoneNumber;
  private String userType;
  private String eventUID;
  private String notificationDTO;
  private String communicationId;
  private Integer clientResponded;
  private String urlDTO;
}
