package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.model.User;
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
public class DocumentIssueNotification extends AbstractMailNotificationEntity {
  private String id;
  private String cnote;
  private Long consignmentId;
  private String clientName;
  private NotificationLocationDTO reporterLocation;
  private NotificationLocationDTO reporteeLocation;
  private NotificationUserDTO reporter;
  private NotificationUserDTO reportee;
  private String scenario;
  private String subReason;
  private NotificationLocationDTO lastScannedAtLocation;
  private NotificationUserDTO lastScannedByUser;

  @Getter
  @Setter
  public static class NotificationLocationDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class NotificationUserDTO {
    private Long id;
    private String name;
    private String email;
    private Long orgId;
    private String type;

    public static void setUserDetails(NotificationUserDTO userDTO, User user) {
      userDTO.setId(user.getId());
      userDTO.setEmail(user.getEmail());
      userDTO.setName(user.getName());
      userDTO.setOrgId(user.getOrganizationId());
    }
  }
}
