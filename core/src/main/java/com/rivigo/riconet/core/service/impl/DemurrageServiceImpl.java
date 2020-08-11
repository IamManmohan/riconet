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

/**
 * DemurrageService is responsible for demurrage related tasks.
 *
 * @author Nikhil Aggarwalki
 * @date 10-Aug-2020
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DemurrageServiceImpl implements DemurrageService {

  /** ZoomBackendAPIClientService is used to make API calls to backend to start/end demurrage. */
  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * ConsignmentReadOnlyService is used to fetch consignment details for validations to be performed
   * for given consignment id.
   */
  private final ConsignmentReadOnlyServiceImpl consignmentReadOnlyService;

  /** DemurrageRepository is used to fetch any existing entries for given consignment id. */
  private final DemurrageRepository demurrageRepository;

  /**
   * Some constants fields to store appropriate details to be required when making certains
   * validations.
   */
  private static final String NORMAL_CNOTE_TYPE = "NORMAL";

  private static final String RIVIGO_ORGANIZATION_ID = "1";
  private static final String DELIVERED_STATUS = "DELIVERED";
  private static final int IS_ACTIVE_CONSIGNMENT = 1;
  private static final String DELIVERY_REATTEMPT_CHARGEABLE_TRUE = "1";

  /**
   * Function used to start demurrage for given consignment. <br>
   * This function is called when event CN_UNDELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and after
   * validation, makes an API call to backend service to start demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
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

    /*
     * For new entry, Delivery reattempt flag must be true for backend call to be made to start demurrage.
     * If entry already exists in demurrage table, then backend call must be always made to appropriately
     * update existing entry. Some other validations also done using separate functions.
     */
    if (!(DELIVERY_REATTEMPT_CHARGEABLE_TRUE.equals(deliveryReattemptChargeable)
            || existingDemurrage != null)
        || !isCnDemurrageValid(consignmentId, startTime)) {
      log.debug("Cnote {} not valid for start demurrage request.", cnote);
      return;
    }
    zoomBackendAPIClientService.startDemurrage(cnote, startTime, undeliveredCnRecordId);
  }

  /**
   * Function used to end demurrage for given consignment. <br>
   * This function is called when event CN_DELIVERY is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and after
   * validation, makes an API call to backend service to end demurrage for given consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
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
    /*
     * Some validations to made before backend call to mark demurrage as completed is done.
     * Consignment must have status DELIVERED and deliveryDateTime not null.
     */
    if (!isCnCorporateTBB(metadata)
        || deliveryDateTime == null
        || !DELIVERED_STATUS.equals(status)) {
      log.debug("Cnote {} not valid for end demurrage request.", cnote);
      return;
    }
    zoomBackendAPIClientService.endDemurrage(cnote);
  }

  /**
   * Demurrage is valid for only CORPORATE TBB consignments. Hence this functions makes these
   * validations at time of marking demurrage completed.
   */
  private boolean isCnCorporateTBB(Map<String, String> metadata) {
    String cnoteType = metadata.get(ZoomCommunicationFieldNames.CNOTE_TYPE.name());
    String organizationId = metadata.get(ZoomCommunicationFieldNames.ORGANIZATION_ID.name());
    return NORMAL_CNOTE_TYPE.equals(cnoteType) && RIVIGO_ORGANIZATION_ID.equals(organizationId);
  }

  /**
   * Function used to make validations at time of starting demurrage for cn. <br>
   * Consignment must be CORPORATE TBB i.e. CnoteType NORMAL and orgId 1. <br>
   * startTime must be after CPD of consignment. <br>
   * Consignment must be active consignment.
   */
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
