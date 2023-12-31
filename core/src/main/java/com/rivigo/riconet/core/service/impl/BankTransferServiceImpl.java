package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.UniqueTransactionReferencePostingDTO;
import com.rivigo.finance.zoom.enums.UniqueTransactionReferencePostingStatus;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketSource;
import com.rivigo.riconet.core.service.BankTransferService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.UploadedFileRecordService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/** BankTransferService is used to handle all tasks related to payment type BANK_TRANSFER. */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankTransferServiceImpl implements BankTransferService {

  private final TicketingService ticketingService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  private final UploadedFileRecordService uploadedFileRecordService;

  private final ObjectMapper objectMapper;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  /**
   * This function is used to create UTR and cnote level ticket in zoom-ticketing for payment type
   * Bank Transfer. <br>
   * This flow ensures Backward compatibility.
   */
  @Override
  public void createTicket(Map<String, String> metadata) {

    Long consignmentId =
        Long.parseLong(metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name()));
    ConsignmentReadOnly consignment = consignmentReadOnlyService.findRequiredById(consignmentId);
    String s3Url = getS3Url(consignment);

    // Create ticket for UTR
    Long utrTicketId = createUTRTicket(consignment.getCnote(), metadata);
    if (utrTicketId == null) {
      log.info("UTR ticket doesn't exist for given UTR.");
      return;
    }

    // Create ticket for cnote
    zoomTicketingAPIClientService.createTicket(
        getTicketDTOForBankTransfer(consignment.getCnote(), metadata, s3Url, null));
  }

  private Long createUTRTicket(@NotNull String cnote, Map<String, String> metadata) {
    final String utrNo =
        metadata.getOrDefault(
            ZoomCommunicationFieldNames.PaymentDetails.TRANSACTION_REFERENCE_NO.name(), "");

    final List<TicketDTO> utrTicketList =
        zoomTicketingAPIClientService.getByEntityInAndType(
            Collections.singletonList(utrNo),
            String.valueOf(ZoomTicketingConstant.UTR_BANK_TRANSFER_TICKET_TYPE_ID));

    // No new UTR level ticket will be created.
    if (CollectionUtils.isEmpty(utrTicketList)) {
      log.info(
          "Since UTR ticket doesn't already exist, this UTR validation will follow new bank transfer flow.");
      return null;
    }
    // Only already existing tickets will be reopened if they are closed.
    // Ensures backward compatibility.
    final TicketDTO utrTicket = utrTicketList.get(0);
    ticketingService.reopenTicketIfClosed(
        utrTicket,
        String.format(
            ZoomTicketingConstant.CNOTE_ADDED_TO_UTR,
            cnote,
            metadata.getOrDefault(
                ZoomCommunicationFieldNames.PaymentDetails.TOTAL_AMOUNT.name(), "")));

    return utrTicket.getId();
  }

  private GroupDTO getGroupForTicketing() {
    return zoomTicketingAPIClientService.getGroupId(
        ZoomTicketingConstant.HQTR_LOCATION_ID,
        ZoomTicketingConstant.BANK_TRANSFER_GROUP_NAME,
        LocationType.HQTR);
  }

  private String getS3Url(ConsignmentReadOnly consignment) {
    List<UploadedFileRecord> uploadedFiles =
        uploadedFileRecordService.getByEntityAndFileType(
            EntityType.CONSIGNMENT, consignment.getCnote(), FileTypes.BANK_TRANSFER);
    if (CollectionUtils.isEmpty(uploadedFiles)) {
      uploadedFiles =
          uploadedFileRecordService.getByEntityAndFileType(
              EntityType.PICKUP,
              String.valueOf(consignment.getPickupId()),
              FileTypes.BANK_TRANSFER);
    }

    if (CollectionUtils.isEmpty(uploadedFiles)) {
      throw new ZoomException(
          "Bank transfer receipt not found for consignment: %s", consignment.getCnote());
    }
    uploadedFiles.sort(Comparator.comparing(UploadedFileRecord::getId));
    return uploadedFiles.get(uploadedFiles.size() - 1).getS3URL();
  }

  /**
   * Create a bank transfer ticket for single CN
   *
   * @param cnote cnote
   * @param metadata paymentdetails with bankname, utr no, transferredAmount
   * @param s3URL s3 url
   * @return TicketDto
   */
  private TicketDTO getTicketDTOForBankTransfer(
      @NotNull String cnote, Map<String, String> metadata, String s3URL, Long parentId) {
    GroupDTO group = getGroupForTicketing();
    return TicketDTO.builder()
        .entityId(cnote)
        .source(TicketSource.INTERNAL)
        .parentId(parentId)
        .typeId(ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID)
        .subject(
            String.format(
                ZoomTicketingConstant.BANK_TRANSFER_MESSAGE,
                TicketEntityType.CN.name(),
                cnote,
                metadata.getOrDefault(
                    ZoomCommunicationFieldNames.PaymentDetails.BANK_NAME.name(), ""),
                metadata.getOrDefault(
                    ZoomCommunicationFieldNames.PaymentDetails.TRANSACTION_REFERENCE_NO.name(), ""),
                s3URL,
                metadata.getOrDefault(
                    ZoomCommunicationFieldNames.PaymentDetails.TOTAL_AMOUNT.name(), "")))
        .title(
            String.format(
                ZoomTicketingConstant.BANK_TRANSFER_TICKET_TITLE,
                TicketEntityType.CN.name(),
                cnote))
        .assigneeId(group == null ? null : group.getId())
        .assigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP)
        .build();
  }

  /**
   * This function handles incoming UniqueTransactionReferencePosting event from compass. <br>
   * Bases on UniqueTransactionReferencePostingStatus, either knockoff or revert knockoff request is
   * sent to backend.
   */
  @Override
  public void handleUniqueTransactionReferencePostingEvent(String payload) {
    final UniqueTransactionReferencePostingDTO utrPostingDto =
        getUniqueTransactionReferencePostingDto(payload);
    if (utrPostingDto == null) {
      throw new ZoomException("UniqueTransactionReferencePostingDto cannot be null.");
    }
    final String utrNo = utrPostingDto.getUniqueTransactionReferenceNumber();
    final UniqueTransactionReferencePostingStatus utrPostingStatus = utrPostingDto.getStatus();
    if (UniqueTransactionReferencePostingStatus.COMPLETE.equals(utrPostingStatus)) {
      log.info("Knockoff complete request received for UTR: {}", utrNo);
      zoomBackendAPIClientService.knockOffUtrBankTransfer(utrNo);
    } else if (UniqueTransactionReferencePostingStatus.INCOMPLETE.equals(utrPostingStatus)) {
      log.info("Knockoff incomplete request received for UTR: {}", utrNo);
      zoomBackendAPIClientService.revertKnockOffUtrBankTransfer(utrNo);
    } else {
      throw new ZoomException("Invalid UniqueTransactionReferencePostingStatus.");
    }
  }

  /**
   * This function is used to convert input string to UniqueTransactionReferencePostingDto for
   * further actions.
   */
  private UniqueTransactionReferencePostingDTO getUniqueTransactionReferencePostingDto(
      String payload) {
    try {
      return objectMapper.readValue(payload, UniqueTransactionReferencePostingDTO.class);
    } catch (IOException e) {
      log.info("Error occurred while processing message: {}", payload, e);
      return null;
    }
  }
}
