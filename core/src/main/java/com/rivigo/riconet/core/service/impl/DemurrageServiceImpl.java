package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.DemurrageService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.vas.Demurrage;
import com.rivigo.zoom.common.repository.mysql.vas.DemurrageRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DemurrageServiceImpl implements DemurrageService {

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private final ConsignmentReadOnlyServiceImpl consignmentReadOnlyService;

  private final DemurrageRepository demurrageRepository;

  private static final String NORMAL_CNOTE_TYPE = "NORMAL";
  private static final String RIVIGO_ORGANIZATION_ID = "1";
  private static final String DELIVERED_STATUS = "DELIVERED";
  private static final int IS_ACTIVE_CONSIGNMENT = 1;
  private static final String DELIVERY_REATTEMPT_CHARGEABLE_TRUE = "1";

  @Override
  public void processEventToStartDemurrage(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    String startTime = metadata.get(ZoomCommunicationFieldNames.Undelivery.ALERT_CREATED_AT.name());
    log.debug(
        "Start demurrage request for cnote {} starting at time {} received.", cnote, startTime);
    String undeliveredCnRecordId = metadata.get(ZoomCommunicationFieldNames.ID.name());
    String consignmentId = metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name());
    String deliveryReattemptChargeable =
        metadata.get(ZoomCommunicationFieldNames.Undelivery.DELIVERY_REATTEMPT_CHARGEABLE.name());
    Demurrage existingDemurrage =
        demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(
            Long.parseLong(consignmentId));
    if (!(DELIVERY_REATTEMPT_CHARGEABLE_TRUE.equals(deliveryReattemptChargeable)
            || existingDemurrage != null)
        || !isCnDemurrageValid(consignmentId, startTime)) {
      log.debug("Cnote {} not valid for start demurrage request.", cnote);
      return;
    }
    zoomBackendAPIClientService.startDemurrage(cnote, startTime, undeliveredCnRecordId);
  }

  @Override
  public void processEventToEndDemurrage(NotificationDTO notificationDTO) {
    Map<String, String> metadata = notificationDTO.getMetadata();
    String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    String deliveryDateTime =
        metadata.get(ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name());
    String status = metadata.get(ZoomCommunicationFieldNames.STATUS.name());
    log.debug(
        "End demurrage request for cnote {} delivered at time {} received.",
        cnote,
        deliveryDateTime);
    if (!isCnCorporateTBB(metadata)
        || deliveryDateTime == null
        || !DELIVERED_STATUS.equals(status)) {
      log.debug("Cnote {} not valid for end demurrage request.", cnote);
      return;
    }
    zoomBackendAPIClientService.endDemurrage(cnote);
  }

  private boolean isCnCorporateTBB(Map<String, String> metadata) {
    String cnoteType = metadata.get(ZoomCommunicationFieldNames.CNOTE_TYPE.name());
    String organizationId = metadata.get(ZoomCommunicationFieldNames.ORGANIZATION_ID.name());
    return NORMAL_CNOTE_TYPE.equals(cnoteType) && RIVIGO_ORGANIZATION_ID.equals(organizationId);
  }

  private boolean isCnDemurrageValid(String consignmentId, String startTime) {
    Optional<ConsignmentReadOnly> consignmentReadOnlyOptional =
        consignmentReadOnlyService.findConsignmentById(Long.parseLong(consignmentId));
    ConsignmentReadOnly cn = null;
    if (consignmentReadOnlyOptional.isPresent()) {
      cn = consignmentReadOnlyOptional.get();
    } else {
      return false;
    }
    return cn.getPromisedDeliveryDateTime().isBefore(new DateTime(Long.parseLong(startTime)))
        && NORMAL_CNOTE_TYPE.equals(cn.getCnoteType().toString())
        && RIVIGO_ORGANIZATION_ID.equals(cn.getOrganizationId().toString())
        && IS_ACTIVE_CONSIGNMENT == cn.getIsActive();
  }
}
