package com.rivigo.riconet.core.dto.notification;

import com.rivigo.zoom.common.enums.PickupNotificationType;
import com.rivigo.zoom.common.enums.PickupStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PickupNotification {

  private String id;
  private Long pickupId;
  private PickupStatus status;
  private Long businessPartnerId;
  private String bpName;
  private Long userId;
  private String userName;
  private String userMobile;
  private Long pickupDate;
  private String pickupTimeSlot;
  private String clientCode;
  private String clientName;
  private PickupNotificationType notificationType;
  private Long locationId;
  private String locationCode;
  private String locationName;
  private String pincode;
  private String consignorMobile;
  private String vehicleNumber;
  private String weight;
  private String smsString;
  private Long reachedAtClientWareHouseTime;
  private List<Recipient> recipients = new ArrayList<>();
  private String contactPerson;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Recipient {
    private Long userId;
    private String mobile;
    private String smsResponse;
  }
}
