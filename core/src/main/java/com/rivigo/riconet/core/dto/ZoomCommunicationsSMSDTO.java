package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by aditya on 23/2/18.
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ZoomCommunicationsSMSDTO {
  private final Boolean confidential = Boolean.TRUE;

  private String message;
  private String phoneNumbers;
  private String userType;
  private String eventUId;
  private String notificationDTO;
  private String communicationId;
  private Boolean clientResponded;
  private String urlDTO;

}