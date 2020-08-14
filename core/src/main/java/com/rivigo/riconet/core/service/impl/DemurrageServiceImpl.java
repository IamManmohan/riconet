package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
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
 * DemurrageService is responsible for demurrage related tasks. <br>
 * To summarise: Demurrage End time will be set as FINAL DELIVERY TIME, and Start time will be set
 * as FIRST DELIVERY FAILURE DUE TO CLIENT FACING REASONS.
 *
 * @author Nikhil Aggarwal
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
   * Constants field to store Delivery reattempt chargeable flag required when making certain
   * validations.
   */
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
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    final String startTime =
        metadata.get(ZoomCommunicationFieldNames.Undelivery.UNDELIVERED_AT.name());
    log.debug(
        "Start demurrage request for cnote {} starting at time {} received.", cnote, startTime);
    final String consignmentId = metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name());
    final String deliveryReattemptChargeable =
        metadata.get(ZoomCommunicationFieldNames.Undelivery.DELIVERY_REATTEMPT_CHARGEABLE.name());
    final Demurrage existingDemurrage =
        demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(
            Long.valueOf(consignmentId));

    /*
     * Condition A: Delivery Reattempt Chargeable flag is true.
     * Condition B: Demurrage entry already exists for cnote.
     * Condition C: isCnDemurrageValid()
     * We want to hit backend api when ((A or B) and C) is true.
     * (A or B) is necessary to cover the cases where consignment is undelivered again
     * after being delivered (i.e. its demurrage is marked as completed). It is possible
     * that consignment may not be undelivered second time for client facing reasons.
     */
    if (!((DELIVERY_REATTEMPT_CHARGEABLE_TRUE.equals(deliveryReattemptChargeable)
            || existingDemurrage != null)
        && isCnDemurrageValid(consignmentId, startTime))) {
      log.debug("Cnote {} not valid for start demurrage request.", cnote);
      return;
    }
    final String undeliveredCnRecordId = metadata.get(ZoomCommunicationFieldNames.ID.name());
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
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    final String deliveryDateTime =
        metadata.get(ZoomCommunicationFieldNames.Consignment.DELIVERY_DATE_TIME.name());
    final String status = metadata.get(ZoomCommunicationFieldNames.STATUS.name());
    final String consignmentId = metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name());
    log.debug(
        "End demurrage request for cnote {} delivered at time {} received.",
        cnote,
        deliveryDateTime);
    final Demurrage existingDemurrage =
        demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(
            Long.valueOf(consignmentId));
    /*
     * Some validations to made before backend call to mark demurrage as completed is done.
     * Consignment must have status DELIVERED and deliveryDateTime not null. Consignment must
     * have existing demurrage entry in table to make end api call.
     */
    if (deliveryDateTime == null
        || !ConsignmentConstant.DELIVERED_STATUS.equals(status)
        || !isCnCorporateTBB(metadata)
        || existingDemurrage == null) {
      log.debug("Cnote {} not valid for end demurrage request.", cnote);
      return;
    }
    zoomBackendAPIClientService.endDemurrage(cnote);
  }

  /**
   * Demurrage is valid for only CORPORATE TBB consignments. Hence this functions makes these
   * validations at time of marking demurrage completed.
   */
  private static boolean isCnCorporateTBB(Map<String, String> metadata) {
    final String cnoteType = metadata.get(ZoomCommunicationFieldNames.CNOTE_TYPE.name());
    final long organizationId =
        Long.parseLong(metadata.get(ZoomCommunicationFieldNames.ORGANIZATION_ID.name()));
    return ConsignmentConstant.NORMAL_CNOTE_TYPE.equals(cnoteType)
        && ConsignmentConstant.RIVIGO_ORGANIZATION_ID == organizationId;
  }

  /**
   * Function used to make validations at time of starting demurrage for cn. <br>
   * Consignment must be CORPORATE TBB i.e. CnoteType NORMAL and orgId 1. <br>
   * startTime must be after CPD of consignment. <br>
   * Consignment must be active consignment.
   */
  private boolean isCnDemurrageValid(String consignmentId, String startTime) {
    final Optional<ConsignmentReadOnly> consignmentReadOnlyOptional =
        consignmentReadOnlyService.findConsignmentById(Long.valueOf(consignmentId));
    ConsignmentReadOnly cn = null;
    if (consignmentReadOnlyOptional.isPresent()) {
      cn = consignmentReadOnlyOptional.get();
    } else {
      return false;
    }
    return cn.getPromisedDeliveryDateTime().isBefore(new DateTime(Long.parseLong(startTime)))
        && ConsignmentConstant.NORMAL_CNOTE_TYPE.equals(cn.getCnoteType().toString())
        && ConsignmentConstant.RIVIGO_ORGANIZATION_ID == cn.getOrganizationId()
        && ConsignmentConstant.IS_ACTIVE_CONSIGNMENT == cn.getIsActive();
  }

  /**
   * Function used to cancel ongoing demurrage for given consignment. <br>
   * This function is called when event CN_DELETED is triggered. <br>
   * This function is used to fetch approriate fields from input NotificationDTO and after
   * validation, makes an API call to backend service to cancel ongoing demurrage for given
   * consignment.
   *
   * @param notificationDTO event payload populated with all the required details.
   */
  @Override
  public void processEventToCancelDemurrage(NotificationDTO notificationDTO) {
    final Map<String, String> metadata = notificationDTO.getMetadata();
    final String cnote = metadata.get(ZoomCommunicationFieldNames.CNOTE.name());
    log.debug("Cancel ongoing demurrage request for cnote {} received.", cnote);
    final String consignmentId = metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name());
    final Demurrage existingDemurrage =
        demurrageRepository.findDemurrageByConsignmentIdAndIsActiveTrue(
            Long.valueOf(consignmentId));
    /*
     * Some validations to be made before backend call to cancel demurrage is done.
     * Consignment must have ongoing demurrage, hence an active entry in demurrage table.
     */
    if (existingDemurrage == null) {
      log.debug("Cnote {} does not have any ongoing demurrage.", cnote);
      return;
    }
    zoomBackendAPIClientService.cancelDemurrage(cnote);
  }
}
