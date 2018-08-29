package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketEntityType;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
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

  @Autowired private PickupService pickupService;

  @Autowired private TicketingService ticketingService;

  public void processNotification(NotificationDTO notificationDTO) {
    EventName eventName = notificationDTO.getEventName();
    switch (eventName) {
      case CN_DELIVERY:
      case CN_DELETED:
        String entityId =
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        ticketingClientService.closeTicket(entityId, TicketEntityType.CN.name(), eventName);
        break;
      case PICKUP_COMPLETION:
      case PICKUP_CANCELLATION:
        ticketingClientService.closeTicket(
            notificationDTO.getEntityId().toString(), TicketEntityType.PRQ.name(), eventName);
        break;
      case CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU:
        ConsignmentBasicDTO loadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeLoadingEvent(loadingData);
        break;
      case CN_RECEIVED_AT_OU:
        ConsignmentBasicDTO unloadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeUnloadingEvent(unloadingData);
        consignmentService.triggerBfCpdCalcualtion(unloadingData);
        break;
      case CN_DELIVERY_LOADED:
        ConsignmentBasicDTO deliveryUnloadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeLoadingEvent(deliveryUnloadingData);
        break;
      case CN_COMPLETION_ALL_INSTANCES:
        ConsignmentCompletionEventDTO completionData = getConsignmentCompletionDTO(notificationDTO);
        qcService.consumeCompletionEvent(completionData);
        break;
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
      case QC_TICKET_ACTION:
        qcService.consumeQcBlockerTicketClosedEvent(
            notificationDTO.getEntityId(),
            getLong(notificationDTO, ZoomCommunicationFieldNames.LAST_UPDATED_BY_ID.name())
                .orElse(null));
        break;
      case TICKET_CREATION:
        ticketingService.sendTicketingEventsEmail(notificationDTO);
        qcService.consumeQcBlockerTicketCreationEvent(
            notificationDTO.getEntityId(),
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()),
            getLong(notificationDTO, ZoomCommunicationFieldNames.TYPE_ID.name()).orElse(null));
        break;
      case TICKET_ASSIGNEE_CHANGE:
      case TICKET_STATUS_CHANGE:
      case TICKET_ESCALATION_CHANGE:
      case TICKET_CC_NEW_PERSON_ADDITION:
      case TICKET_SEVERITY_CHANGE:
      case TICKET_COMMENT_CREATION:
        ticketingService.sendTicketingEventsEmail(notificationDTO);
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
        .fromId(
            getLong(notificationDTO, ZoomCommunicationFieldNames.FROM_LOCATION_ID.name())
                .orElse(null))
        .status(status)
        .build();
  }

  public Optional<Long> getLong(NotificationDTO notificationDTO, String fieldName) {
    try {
      return Optional.of(Long.parseLong(notificationDTO.getMetadata().get(fieldName)));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private ConsignmentCompletionEventDTO getConsignmentCompletionDTO(
      NotificationDTO notificationDTO) {
    return ConsignmentCompletionEventDTO.builder()
        .cnote(notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name()))
        .consignmentId(
            Long.parseLong(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name())))
        .build();
  }
}
