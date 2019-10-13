package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.enums.DEPSLocationType;
import com.rivigo.zoom.common.enums.DEPSScenario;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import java.util.List;
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
public class DEPSNotification extends AbstractMailNotificationEntity {
  private String id;
  private List<Long> depsIdList;
  private Long taskId;
  private String cnote;
  private Long consignmentId;
  private String clientName;
  private Long bookingDateTime;
  private Long clientPromisedDeliveryDateTime;
  private Long fromLocationId;
  private String fromLocationName;
  private String fromLocationCode;
  private int originalNumberOfBoxes;
  private int depsBoxesCount;
  private int resolvedCount;
  private Double invoiceValue;
  private Double expectedLoss;
  private TaskType depsTaskType;
  private DEPSLocationDTO reporterLocation;
  private DEPSLocationDTO reporteeLocation;
  private DEPSUserDTO reporter;
  private DEPSUserDTO reportee;
  private DEPSScenario scenario;
  private DEPSLocationDTO lastScannedAtLocation;
  private DEPSUserDTO lastScannedByUser;

  @Getter
  @Setter
  public static class DEPSLocationDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private DEPSLocationType depsLocationType;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DEPSUserDTO {
    private Long id;
    private String name;
    private String email;
    private Long orgId;
    private DEPSLocationType locationType;
    private String type;

    public static void setFeederVendorDetails(
        DEPSNotification dto, DEPSUserDTO userDTO, FeederVendor feederVendor) {
      userDTO.setId(feederVendor.getId());
      userDTO.setEmail(feederVendor.getEmail());
      userDTO.setLocationType(DEPSLocationType.OUTBOUND);
      userDTO.setName(feederVendor.getVendorName());
    }

    public static void setUserDetails(DEPSNotification dto, DEPSUserDTO userDTO, User user) {
      userDTO.setId(user.getId());
      userDTO.setEmail(user.getEmail());
      userDTO.setLocationType(DEPSLocationType.OUTBOUND);
      userDTO.setName(user.getName());
      userDTO.setOrgId(user.getOrganizationId());
    }
  }
}
