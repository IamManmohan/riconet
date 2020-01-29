package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.zoom.common.dto.errorcorrection.ConsignmentQcDataDTO;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 19/4/18. */
@Slf4j
@Service
public class EventTriggerService {

  @Autowired private TicketingClientService ticketingClientService;

  @Autowired private QcService qcService;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private ChequeBounceService chequeBounceService;

  @Autowired private TicketingService ticketingService;

  @Autowired private AppNotificationService appNotificationService;

  @Autowired private TicketActionFactory ticketActionFactory;

  @Autowired private RTOService rtoService;

  @Autowired private BankTransferService bankTransferService;

  @Autowired private ConsignmentQcErrorCorrectionService consignmentQcService;

  public void processNotification(NotificationDTO notificationDTO) {
    EventName eventName = EventName.valueOf(notificationDTO.getEventName());
    String entityId;
    switch (eventName) {
      case CN_DELIVERY:
        appNotificationService.sendCnDeliveredNotification(notificationDTO);
      case CN_TRIP_DISPATCHED:
        entityId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        ticketingClientService.autoCloseTicket(
            entityId, TicketEntityType.CN.name(), eventName.name());
        Optional.ofNullable(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.FIRST_RIVIGO_OU.name()))
            .ifPresent(
                b -> appNotificationService.sendCnFirstOuDispatchNotification(notificationDTO));
        break;
      case CN_PAYMENT_HANDOVER_COMPLETED:
        entityId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        ticketingClientService.autoCloseTicket(
            entityId, TicketEntityType.CN.name(), eventName.name());
        break;
      case CN_STALE:
        String staleCategory =
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.STALE_CATEGORY.name());
        entityId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        String staleCategoryEventName =
            eventName + ZoomTicketingConstant.UNDERSCORE + staleCategory;
        ticketingClientService.autoCloseTicket(
            entityId, TicketEntityType.CN.name(), staleCategoryEventName);
        break;
      case CN_DELETED:
        entityId = notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.OLD_CNOTE.name());
        ticketingClientService.autoCloseTicket(
            entityId, TicketEntityType.CN.name(), eventName.name());
        break;
      case PICKUP_COMPLETION:
      case PICKUP_CANCELLATION:
        ticketingClientService.autoCloseTicket(
            notificationDTO.getEntityId().toString(),
            TicketEntityType.PRQ.name(),
            eventName.name());
        break;
      case CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU:
        ConsignmentBasicDTO loadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeLoadingEvent(loadingData);
        break;
      case CN_RECEIVED_AT_OU:
        processCNReceivedAtOuAndHandleException(notificationDTO);
        rtoService.reassignRTOTicketIfExists(notificationDTO);
        break;
      case CN_DRS_DISPATCH:
        appNotificationService.sendCnDrsDispatchEvent(notificationDTO);
      case CN_DELIVERY_LOADED:
        ConsignmentBasicDTO deliveryUnloadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeDeliveryLoadingEvent(deliveryUnloadingData);
        break;
        //      For reference: T4340
        //      case CN_COMPLETION_ALL_INSTANCES:
        //        ConsignmentCompletionEventDTO completionData =
        // getConsignmentCompletionDTO(notificationDTO);
        //        qcService.consumeCompletionEvent(completionData);
        //        break;
      case CN_CNOTE_TYPE_CHANGED_FROM_NORMAL:
        ConsignmentBasicDTO consignment = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeCnoteTypeChangeEvent(consignment);
        break;
      case COLLECTION_CHEQUE_BOUNCE:
        chequeBounceService.consumeChequeBounceEvent(notificationDTO);
        break;
      case CN_CNOTE_CHANGE:
        qcService.consumeCnoteChangeEvent(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.OLD_CNOTE.name()),
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()));
        break;
      case CN_DEPS_CREATION:
      case CN_DEPS_CREATION_FROM_CONSIGNMENT_HISTORY:
        qcService.consumeDepsCreationEvent(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()),
            notificationDTO.getEntityId());
        break;
      case TICKET_ACTION:
        qcService.consumeQcBlockerTicketClosedEvent(
            notificationDTO.getEntityId(),
            getLong(notificationDTO, ZoomCommunicationFieldNames.LAST_UPDATED_BY_ID.name())
                .orElse(null),
            getString(notificationDTO, ZoomCommunicationFieldNames.ACTION_NAME.name())
                .orElse(null));
        ticketActionFactory.consume(
            notificationDTO.getEntityId(),
            getString(notificationDTO, ZoomCommunicationFieldNames.TICKET_ENTITY_ID.name())
                .orElse(null),
            getString(notificationDTO, ZoomCommunicationFieldNames.ACTION_NAME.name()).orElse(null),
            getString(notificationDTO, ZoomCommunicationFieldNames.ACTION_VALUE.name())
                .orElse(null));
        break;
      case BANK_TRANSFER_INITIATED:
        bankTransferService.createTicket(notificationDTO.getMetadata());
        break;
      case TICKET_CREATION:
        qcService.consumeQcBlockerTicketCreationEvent(
            notificationDTO.getEntityId(),
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.TICKET_ENTITY_ID.name()),
            getLong(notificationDTO, ZoomCommunicationFieldNames.TYPE_ID.name()).orElse(null));
        ticketingService.setPriorityMapping(notificationDTO);
        //        ticketingService.sendTicketingEventsEmail(notificationDTO);
        break;
      case RTO_TICKET_ASSIGNEE_CHANGE:
        rtoService.processRTOAsigneeChangeEvent(notificationDTO);
        break;
      case TICKET_ASSIGNEE_CHANGE:
      case TICKET_STATUS_CHANGE:
      case TICKET_ESCALATION_CHANGE:
      case TICKET_CC_NEW_PERSON_ADDITION:
      case TICKET_SEVERITY_CHANGE:
      case TICKET_COMMENT_CREATION:
        //        ticketingService.sendTicketingEventsEmail(notificationDTO);
        break;
      case CONSIGNMENT_EWAYBILL_METADATA_CREATION_ADDRESS_CLEANUP:
        //        datastoreService.cleanupAddressesUsingEwaybillMetadata(notificationDTO);
        break;
      case CONSIGNMENT_QC_DATA_UPSERT:
        Optional<Long> consignmenQcDataId =
            getLong(notificationDTO, ConsignmentQcDataDTO.consignmenQcDataIdKey);
        Optional<String> qcDevianceCategory =
            getString(notificationDTO, ConsignmentQcDataDTO.qcDevianceCategoryKey);
        if (consignmenQcDataId.isPresent() && qcDevianceCategory.isPresent()) {
          consignmentQcService.processConsignmentQcDataEvent(
              consignmenQcDataId.get(), qcDevianceCategory.get());
        }
        break;
      default:
        log.info("Event does not trigger anything {}", eventName);
    }
  }

  private ConsignmentBasicDTO getBasicConsignmentDTO(NotificationDTO notificationDTO) {
    ConsignmentStatus status =
        notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.STATUS.name()) == null
            ? null
            : ConsignmentStatus.valueOf(
                notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.STATUS.name()));
    return ConsignmentBasicDTO.builder()
        .cnote(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()))
        .consignmentId(
            getLong(notificationDTO, ZoomCommunicationFieldNames.CONSIGNMENT_ID.name())
                .orElse(null))
        .locationId(
            getLong(notificationDTO, ZoomCommunicationFieldNames.LOCATION_ID.name()).orElse(null))
        .toLocationId(
            getLong(notificationDTO, ZoomCommunicationFieldNames.TO_LOCATION_ID.name())
                .orElse(null))
        .fromId(
            getLong(notificationDTO, ZoomCommunicationFieldNames.FROM_LOCATION_ID.name())
                .orElse(null))
        .status(status)
        .build();
  }

  private Optional<Long> getLong(NotificationDTO notificationDTO, String fieldName) {
    try {
      return Optional.of(Long.parseLong(notificationDTO.getMetadata().get(fieldName)));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Optional<String> getString(NotificationDTO notificationDTO, String fieldName) {
    try {
      return Optional.of((notificationDTO.getMetadata().get(fieldName)));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private ConsignmentCompletionEventDTO getConsignmentCompletionDTO(
      NotificationDTO notificationDTO) {
    return ConsignmentCompletionEventDTO.builder()
        .cnote(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()))
        .isRTOCnote(
            notificationDTO
                .getMetadata()
                .containsKey(ZoomCommunicationFieldNames.FORWARD_CONSIGNMENT_ID.name()))
        .consignmentId(
            Long.parseLong(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name())))
        .build();
  }

  private void processCNReceivedAtOuAndHandleException(NotificationDTO notificationDTO) {
    ConsignmentBasicDTO unloadingData = getBasicConsignmentDTO(notificationDTO);
    // consignmentService.triggerAssetCnUnload(notificationDTO, unloadingData);
    try {
      qcService.consumeUnloadingEvent(unloadingData);
    } catch (Exception e) {
      log.error("QC service failed", e);
    }
    try {
      consignmentService.triggerBfCpdCalcualtion(unloadingData);
    } catch (Exception e) {
      log.error("BF CPD calculation failed", e);
    }
  }
}
