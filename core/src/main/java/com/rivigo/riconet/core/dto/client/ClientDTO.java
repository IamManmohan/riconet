package com.rivigo.riconet.core.dto.client;

import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.util.commons.enums.Unit;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/** Created by ashfakh on 18/6/18. */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
  private Long id;
  private String clientCode;
  private String oldClientCode;
  private String name;
  private String displayName;
  private OperationalStatus status;
  private IndustryTypeDTO industryType;
  private String billingName;
  private Long samUserId;
  private Long samLeadId;
  private CnoteType cnoteType;
  private Boolean hasBillingEntity;
  private List<BillingEntityDTO> billingEntities;
  private Boolean insuranceReqd;
  private Boolean blockedPaidForDelivery;
  private Boolean blockedToPayForDelivery;
  private Long organizationId;
  private String organizationName;
  private Object clientVasDetailDTO;
  private Boolean blockedDamagePilferageCns;
  private Boolean blockedShortageCns;
  private DateTime lastUpdatedAt;
  private String rbm;
  private String cbm;
  private String bdm;
  private Boolean ewaybillExempted;
  private Unit defaultVolumeUnit;
  private Boolean fodApplicable;
  private Boolean rtoApplicable;
  private Boolean laneRateBypass;
  private Set<String> notificationToList;
  private Set<String> notificationCcList;
  private Set<String> clientNotificationList;
  private Boolean isOnlineEnabled;
  private Boolean isDacc;
}
