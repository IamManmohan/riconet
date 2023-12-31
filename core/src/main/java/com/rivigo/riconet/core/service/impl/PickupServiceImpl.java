package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.notification.PickupNotification;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.StockAccumulatorService;
import com.rivigo.riconet.core.service.ZoomBookAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.PickupNotificationType;
import com.rivigo.zoom.common.enums.PickupStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import com.rivigo.zoom.common.enums.ZoomUserType;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class PickupServiceImpl implements PickupService {

  @Autowired private PickupRepository pickupRepository;

  @Autowired private ClientMasterService clientMasterService;

  @Autowired private StockAccumulatorService stockAccumulatorService;

  @Autowired private ZoomUserMasterService zoomUserMasterService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private LocationService locationService;

  @Autowired private SmsService smsService;

  @Autowired private ConsignmentReadOnlyService consignmentReadOnlyService;

  @Autowired private ZoomBookAPIClientService zoomBookAPIClientService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ConsignmentService consignmentService;

  @Override
  public Map<Long, Pickup> getPickupMapByIdIn(List<Long> pickupTripIdList) {
    return ((List<Pickup>) pickupRepository.findAll(pickupTripIdList))
        .stream()
        .collect(Collectors.toMap(Pickup::getId, Function.identity()));
  }

  @Override
  public void processPickupNotificationDTOList(
      List<PickupNotificationDTO> pickupNotificationDTOList) {
    if (CollectionUtils.isEmpty(pickupNotificationDTOList)) {
      return;
    }
    if (pickupNotificationDTOList.size() == 1
        && pickupNotificationDTOList.get(0).getId() == null
        && PickupNotificationType.PICKUP_DELAYED.equals(
            pickupNotificationDTOList.get(0).getNotificationType())) {
      processDelayedPickups(pickupNotificationDTOList.get(0).getLastUpdatedAt());
      return;
    }
    Map<Long, Pickup> pickupMap =
        getPickupMapByIdIn(
            pickupNotificationDTOList
                .stream()
                .map(PickupNotificationDTO::getId)
                .collect(Collectors.toList()));
    String locationCodes =
        zoomPropertyService.getString(ZoomPropertyName.PICKUP_NOTIFICATION_ALLOWED_LOCATIONS);
    pickupNotificationDTOList
        .stream()
        .filter(pickupNotificationDTO -> pickupMap.containsKey(pickupNotificationDTO.getId()))
        .forEach(
            pickupNotificationDTO -> {
              PickupNotification pickupNotification =
                  getPickupNotification(
                      pickupMap.get(pickupNotificationDTO.getId()),
                      pickupNotificationDTO.getLastUpdatedAt(),
                      pickupNotificationDTO.getNotificationType());
              if (pickupNotification == null) {
                return;
              }

              if (locationCodes == null
                  || locationCodes.contains(pickupNotification.getLocationCode())) {
                sendSms(pickupNotification, getSmsTemplate(pickupNotification));
              }
            });
  }

  private void processDelayedPickups(Long lastExecutedAt) {
    List<Pickup> pickupList =
        pickupRepository.findByPickupStatusInAndUserIdNotNull(Arrays.asList(PickupStatus.CREATED));
    Long prevCutOffTime =
        lastExecutedAt
            + zoomPropertyService.getInteger(
                    ZoomPropertyName.PICKUP_DELAY_NOTIFICATION_SECONDS, 15 * 60)
                * 1000l;
    Long cutOffTime =
        DateTime.now().getMillis()
            + zoomPropertyService.getInteger(
                    ZoomPropertyName.PICKUP_DELAY_NOTIFICATION_SECONDS, 15 * 60)
                * 1000l;
    List<Pickup> delayedPickupList =
        pickupList
            .stream()
            .filter(
                pickup ->
                    ((prevCutOffTime < pickup.getPickupEndTime()
                            || lastExecutedAt < pickup.getLastUpdatedAt().getMillis())
                        && pickup.getPickupEndTime() < cutOffTime))
            .collect(Collectors.toList());
    String smsTemplate = zoomPropertyService.getString(ZoomPropertyName.PICKUP_DELAYED_SMS_STRING);
    delayedPickupList.forEach(
        pickup -> {
          PickupNotification pickupNotification =
              getPickupNotification(
                  pickup,
                  pickup.getLastUpdatedAt().getMillis(),
                  PickupNotificationType.PICKUP_DELAYED);
          if (pickupNotification != null) {
            sendSms(pickupNotification, smsTemplate);
          }
        });
  }

  private PickupNotification getPickupNotification(
      Pickup pickup, Long lastUpdatedAt, PickupNotificationType type) {
    PickupNotification pickupNotification = new PickupNotification();
    if (lastUpdatedAt > pickup.getLastUpdatedAt().getMillis()) {
      throw new ZoomException("Pickup is not saved properly");
    }
    String id = getUniqueId(pickup, type);
    pickupNotification.setId(id);
    pickupNotification.setPickupId(pickup.getId());
    pickupNotification.setStatus(pickup.getPickupStatus());
    pickupNotification.setBusinessPartnerId(pickup.getBusinessPartnerId());
    pickupNotification.setBpName(
        pickup.getBusinessPartner() == null ? null : pickup.getBusinessPartner().getName());
    pickupNotification.setUserName(pickup.getUser() == null ? null : pickup.getUser().getName());
    pickupNotification.setUserMobile(
        pickup.getUser() == null ? null : pickup.getUser().getMobileNo());
    pickupNotification.setUserId(pickup.getUserId());
    pickupNotification.setPickupDate(pickup.getPickupDate().getMillis());
    pickupNotification.setPickupTimeSlot(pickup.getPickupTimeSlot());
    pickupNotification.setNotificationType(type);
    pickupNotification.setLocationId(pickup.getLocationId());
    pickupNotification.setPincode(pickup.getPincode());
    pickupNotification.setVehicleNumber(pickup.getVehicleNumber());
    pickupNotification.setWeight(pickup.getWeightRange());
    pickupNotification.setContactPerson(pickup.getContactPerson());
    pickupNotification.setConsignorMobile(pickup.getContactNumber());
    pickupNotification.setReachedAtClientWareHouseTime(
        PickupStatus.REACHED_AT_CLIENT_WAREHOUSE.equals(pickup.getPickupStatus())
            ? pickup.getLastUpdatedAt().getMillis()
            : null);
    Client client = clientMasterService.getClientById(pickup.getClientId());
    if (client == null) {
      throw new ZoomException("No client with this Id exists");
    }
    Location loc = locationService.getLocationById(pickup.getLocationId());
    pickupNotification.setLocationName(loc.getName());
    pickupNotification.setLocationCode(loc.getCode());
    pickupNotification.setClientName(client.getName());
    pickupNotification.setClientCode(client.getClientCode());
    fillRecipients(pickupNotification);
    return pickupNotification;
  }

  private String getUniqueId(Pickup pickup, PickupNotificationType type) {
    if (PickupNotificationType.PICKUP_ASSIGNED.equals(type)) {
      return pickup.getId()
          + "|"
          + type.name()
          + "|"
          + pickup.getBusinessPartnerId()
          + "|"
          + pickup.getUserId()
          + "|"
          + pickup.getLastUpdatedAt();
    } else {
      return pickup.getId()
          + "|"
          + type.name()
          + "|"
          + pickup.getPickupDate().getMillis()
          + "|"
          + pickup.getPickupTimeSlot();
    }
  }

  private void fillBpAdminRecipients(PickupNotification pickupNotification) {
    if (pickupNotification.getBusinessPartnerId() == null) {
      return;
    }
    List<StockAccumulator> saList =
        stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(
            StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN,
            pickupNotification.getBusinessPartnerId(),
            OperationalStatus.ACTIVE);
    saList.forEach(
        sa ->
            pickupNotification
                .getRecipients()
                .add(
                    new PickupNotification.Recipient(
                        sa.getUser().getId(), sa.getUser().getMobileNo(), null)));
  }

  private void fillPickupCoordinatorRecipients(PickupNotification pickupNotification) {
    List<ZoomUser> zuList =
        zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(
            pickupNotification.getLocationId(),
            ZoomUserType.ZOOM_PCE.name(),
            ZoomUserType.ZOOM_TECH_SUPPORT.name());
    zuList.forEach(
        sa ->
            pickupNotification
                .getRecipients()
                .add(
                    new PickupNotification.Recipient(
                        sa.getUser().getId(), sa.getUser().getMobileNo(), null)));
  }

  private void fillRecipients(PickupNotification pickupNotification) {
    switch (pickupNotification.getNotificationType()) {
      case PICKUP_REACHED:
        fillBpAdminRecipients(pickupNotification);
        return;
      case PICKUP_DELAYED:
        fillBpAdminRecipients(pickupNotification);
        pickupNotification
            .getRecipients()
            .add(
                new PickupNotification.Recipient(
                    pickupNotification.getUserId(), pickupNotification.getUserMobile(), null));
        return;
      case PICKUP_ASSIGNED:
        if (pickupNotification.getUserId() != null) {
          pickupNotification
              .getRecipients()
              .add(
                  new PickupNotification.Recipient(
                      pickupNotification.getUserId(), pickupNotification.getUserMobile(), null));
        } else {
          fillBpAdminRecipients(pickupNotification);
        }
        return;
      case PICKUP_CREATED:
        if (pickupNotification.getBusinessPartnerId() != null) {
          fillBpAdminRecipients(pickupNotification);
        } else {
          fillPickupCoordinatorRecipients(pickupNotification);
        }
        return;
      default:
        break;
    }
  }

  private String getSmsTemplate(PickupNotification pickupNotification) {
    String smsTemplate = null;
    switch (pickupNotification.getNotificationType()) {
      case PICKUP_REACHED:
        smsTemplate = zoomPropertyService.getString(ZoomPropertyName.PICKUP_REACHED_SMS_STRING);
        break;
      case PICKUP_DELAYED:
        smsTemplate = zoomPropertyService.getString(ZoomPropertyName.PICKUP_DELAYED_SMS_STRING);
        break;
      case PICKUP_ASSIGNED:
        if (pickupNotification.getUserId() != null) {
          smsTemplate =
              zoomPropertyService.getString(ZoomPropertyName.PICKUP_ASSIGNED_TO_USER_SMS_STRING);
        } else {
          smsTemplate =
              zoomPropertyService.getString(ZoomPropertyName.PICKUP_ASSIGNED_BP_USER_SMS_STRING);
        }
        break;
      case PICKUP_CREATED:
        if (pickupNotification.getBusinessPartnerId() != null) {
          smsTemplate =
              zoomPropertyService.getString(ZoomPropertyName.PICKUP_ASSIGNED_BP_USER_SMS_STRING);
        } else {
          smsTemplate = zoomPropertyService.getString(ZoomPropertyName.PICKUP_CREATED_SMS_STRING);
        }
        break;
      default:
        break;
    }
    return smsTemplate;
  }

  private void sendSms(PickupNotification pickupNotification, String smsTemplate) {
    if (smsTemplate == null) {
      log.info("Please add the sms templates");
      return;
    }
    String smsString = designSms(pickupNotification, smsTemplate);
    log.info("smsTemplate: {}", smsTemplate);
    log.info("smsString: {}", smsString);
    pickupNotification.setSmsString(smsString);
    pickupNotification
        .getRecipients()
        .forEach(
            recipient ->
                recipient.setSmsResponse(smsService.sendSms(recipient.getMobile(), smsString)));
  }

  private String designSms(PickupNotification pickupNotification, String template) {
    try {
      log.info("pickupNotification: {}", objectMapper.writeValueAsString(pickupNotification));
    } catch (JsonProcessingException e) {
      log.warn(e.getMessage());
    }
    Map<String, String> valuesMap = new HashMap<>();
    DateTimeFormatter formatter1 =
        DateTimeFormat.forPattern("dd-MM-yyyy ").withZone(DateTimeZone.forID("Asia/Kolkata"));
    String dateStr1 = formatter1.print(pickupNotification.getPickupDate());

    DateTimeFormatter formatter2 =
        DateTimeFormat.forPattern("dd-MM-yyyy HH:mm").withZone(DateTimeZone.forID("Asia/Kolkata"));
    String dateStr2 =
        pickupNotification.getReachedAtClientWareHouseTime() != null
            ? formatter2.print(pickupNotification.getReachedAtClientWareHouseTime())
            : "-";

    valuesMap.put("pickupId", pickupNotification.getPickupId().toString());
    valuesMap.put("bpName", pickupNotification.getBpName());
    valuesMap.put("userName", pickupNotification.getUserName());
    valuesMap.put("userMobile", pickupNotification.getUserMobile());
    valuesMap.put("pickupDate", dateStr1);
    valuesMap.put("reachedAtClientWareHouseTime", dateStr2);
    valuesMap.put("pickupTimeSlot", pickupNotification.getPickupTimeSlot());
    valuesMap.put("clientCode", pickupNotification.getClientCode());
    valuesMap.put("clientName", pickupNotification.getClientName());
    valuesMap.put("locationCode", pickupNotification.getLocationCode());
    valuesMap.put("locationName", pickupNotification.getLocationName());
    valuesMap.put("pincode", pickupNotification.getPincode());
    valuesMap.put("vehicleNumber", pickupNotification.getVehicleNumber());
    valuesMap.put(
        "weight",
        new BigDecimal(pickupNotification.getWeight()).stripTrailingZeros().toPlainString());
    valuesMap.put("consignorMobile", pickupNotification.getConsignorMobile());
    valuesMap.put("contactPerson", pickupNotification.getContactPerson());

    valuesMap = smsService.sanitizeStringValuesForStringLimit(valuesMap);
    StrSubstitutor sub = new StrSubstitutor(valuesMap);
    return sub.replace(template);
  }
}
