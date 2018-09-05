package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketEntityType;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import java.util.concurrent.TimeUnit;
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

  public void processNotification(NotificationDTO notificationDTO) {
    EventName eventName = notificationDTO.getEventName();
    switch (eventName) {
      case CN_DELIVERY:
        String entityId =
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.CNOTE.name());
        ticketingClientService.closeTicket(
            entityId, TicketEntityType.CN.name(), EventName.CN_DELIVERY);
        break;
      case PICKUP_COMPLETION:
        ticketingClientService.closeTicket(
            notificationDTO.getEntityId().toString(),
            TicketEntityType.PRQ.name(),
            EventName.PICKUP_COMPLETION);
        break;
      case CN_STATUS_CHANGE_FROM_RECEIVED_AT_OU:
        ConsignmentBasicDTO loadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeLoadingEvent(loadingData);
        break;
      case CN_RECEIVED_AT_OU:
        ConsignmentBasicDTO unloadingData = getBasicConsignmentDTO(notificationDTO);
        consignmentService.triggerAssetCnUnload(notificationDTO, unloadingData);
        qcService.consumeUnloadingEvent(unloadingData);
        consignmentService.triggerBfCpdCalcualtion(unloadingData);
        break;
      case CN_DELIVERY_LOADED:
        ConsignmentBasicDTO deliveryUnloadingData = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeLoadingEvent(deliveryUnloadingData);
        break;
      case CN_COMPLETION_ALL_INSTANCES:
        ConsignmentCompletionEventDTO completionData = getConsignmentCompletionDTO(notificationDTO);
        try {
          TimeUnit.SECONDS.sleep(2);
          // Fixme: Chirag: This is to ensure that qc is called after cn is created in billing
          // service
          // We need charged weight from billing service as we don't have reliable volume data
        } catch (InterruptedException e) {
          log.warn(e.getMessage());
        }
        qcService.consumeCompletionEvent(completionData);
        break;
      case CN_CNOTE_TYPE_CHANGED_FROM_NORMAL:
        ConsignmentBasicDTO consignment = getBasicConsignmentDTO(notificationDTO);
        qcService.consumeCnoteTypeChangeEvent(consignment);
        break;
      case COLLECTION_CHEQUE_BOUNCE:
        chequeBounceService.consumeChequeBounceEvent(notificationDTO);
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
            Long.parseLong(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name())))
        .locationId(
            Long.parseLong(
                notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.LOCATION_ID.name())))
        .toLocationId(
            Long.parseLong(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.TO_LOCATION_ID.name())))
        .status(status)
        .build();
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
