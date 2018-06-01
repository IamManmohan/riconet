package com.rivigo.riconet.event.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.event.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import com.rivigo.zoom.common.enums.PaymentMode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsignmentBlockUnblockServiceImpl implements ConsignmentBlockUnblockService {

  private final ApiClientService apiClientService;

  @Value("${zoombackend.base.url}")
  private String zoomBackendBaseUrl;

  @Autowired
  public ConsignmentBlockUnblockServiceImpl(ApiClientService apiClientService) {
    this.apiClientService = apiClientService;
  }

  @Override
  public void processNotification(NotificationDTO notificationDTO) {
    switch (notificationDTO.getEventName()) {
      case COLLECTION_CHEQUE_BOUNCE:
        blockCn(notificationDTO);
        break;
      case CN_COLLECTION_CHEQUE_BOUNCE_TICKET_CLOSED:
        unblockCn(notificationDTO);
        break;
      default:
        log.info(
            "Skipping event {} processing from {}",
            notificationDTO.getEventName(),
            this.getClass().getName());
    }
  }

  private void unblockCn(NotificationDTO notificationDTO) {
    blockUnblockRequest(notificationDTO, ConsignmentBlockerRequestType.UNBLOCK);
  }

  private void blockCn(NotificationDTO notificationDTO) {
    if (!PaymentMode.PREPAID
        .name()
        .equalsIgnoreCase(
            notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.PAYMENT_MODE.name()))) {
      log.info(
          "Cheque bounce occurred for to pay cn. So cn {} will not be blocked",
          notificationDTO.getEntityId());
      return;
    }
    blockUnblockRequest(notificationDTO, ConsignmentBlockerRequestType.BLOCK);
  }

  private void blockUnblockRequest(
      NotificationDTO notificationDTO, ConsignmentBlockerRequestType requestType) {
    ConsignmentBlockerRequestDTO requestDTO =
        ConsignmentBlockerRequestDTO.builder()
            .requestType(requestType)
            .isActive(Boolean.TRUE)
            .reason(
                notificationDTO.getMetadata().get(ZoomCommunicationFieldNames.Reason.REASON.name()))
            .subReason(
                notificationDTO
                    .getMetadata()
                    .get(ZoomCommunicationFieldNames.Reason.SUB_REASON.name()))
            .consignmentId(notificationDTO.getEntityId())
            .build();
    try {
      JsonNode responseJson =
          apiClientService.getEntity(
              requestDTO, HttpMethod.POST, "/consignmentBlocker", null, zoomBackendBaseUrl);
      log.debug("response {}", responseJson);
    } catch (IOException e) {
      log.error("Exception occurred while unblocking cn in zoom tech", e);
    }
  }
}
