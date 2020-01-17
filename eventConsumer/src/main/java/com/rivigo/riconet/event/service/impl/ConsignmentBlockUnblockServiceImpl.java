package com.rivigo.riconet.event.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.ChequeBounceDTO;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.CnBlockUnblockEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.event.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.event.service.ConsignmentBlockUnblockService;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsignmentBlockUnblockServiceImpl implements ConsignmentBlockUnblockService {

  private final ApiClientService apiClientService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Value("${zoom.url}")
  private String zoomBackendBaseUrl;

  @Override
  public void processNotification(NotificationDTO notificationDTO) {
    switch (CnBlockUnblockEventName.valueOf(notificationDTO.getEventName())) {
      case COLLECTION_CHEQUE_BOUNCE:
        markRecoveryPending(notificationDTO);
        break;
      case CN_COLLECTION_CHEQUE_BOUNCE_TICKET_CLOSED:
      case CN_SHORTAGE_RESOLUTION:
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

  /**
   * reflect cheque bounced amount in user/bp_book, ou_collection_book and remove from
   * ou_out_standing
   */
  private JsonNode markRecoveryPending(NotificationDTO notificationDTO) {

    Map<String, String> metadata = notificationDTO.getMetadata();
    ChequeBounceDTO chequeBounceDTO =
        ChequeBounceDTO.builder()
            .cnote(metadata.get(ZoomCommunicationFieldNames.CNOTE.name()))
            .chequeNumber(metadata.get(ZoomCommunicationFieldNames.INSTRUMENT_NUMBER.name()))
            .bankName(metadata.get(ZoomCommunicationFieldNames.DRAWEE_BANK.name()))
            .amount(new BigDecimal(metadata.get(ZoomCommunicationFieldNames.AMOUNT.name())))
            .build();
    return zoomBackendAPIClientService.markRecoveryPending(chequeBounceDTO);
  }
}
