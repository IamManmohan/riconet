package com.rivigo.riconet.core.service.impl;

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
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.exceptions.ZoomException;
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

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankTransferServiceImpl implements BankTransferService {

  private final TicketingService ticketingService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  private final UploadedFileRecordService uploadedFileRecordService;

  @Override
  public void createTicket(Map<String, String> metadata) {

    Long consignmentId =
        Long.parseLong(metadata.get(ZoomCommunicationFieldNames.CONSIGNMENT_ID.name()));
    ConsignmentReadOnly consignment = consignmentReadOnlyService.findRequiredById(consignmentId);
    String s3Url = getS3Url(consignment);

    // Create ticket for UTR
    Long utrTicketId = createUTRTicket(consignment.getCnote(), metadata, s3Url);

    // Create ticket for cnote
    TicketDTO ticketDTO =
        zoomTicketingAPIClientService.createTicket(
            getTicketDTOForBankTransfer(consignment.getCnote(), metadata, s3Url, null));
  }

  private Long createUTRTicket(@NotNull String cnote, Map<String, String> metadata, String s3Url) {
    String utrNo =
        metadata.getOrDefault(
            ZoomCommunicationFieldNames.PaymentDetails.TRANSACTION_REFERENCE_NO.name(), "");

    List<TicketDTO> utrTicketList =
        zoomTicketingAPIClientService
            .getByEntityInAndType(
                Collections.singletonList(utrNo),
                String.valueOf(ZoomTicketingConstant.UTR_BANK_TRANSFER_TICKET_TYPE_ID));

    if(CollectionUtils.isEmpty(utrTicketList)){
      log.info("Since UTR ticket doesn't already exist, this UTR validation will follow new bank transfer flow.");
      throw new ZoomException("UTR ticket doesn't exist for UTR number: {}",utrNo);
    }

    TicketDTO utrTicket = utrTicketList.get(0);
    ticketingService.reopenTicketIfClosed(
        utrTicket,
        String.format(
            ZoomTicketingConstant.CNOTE_ADDED_TO_UTR,
            cnote,
            metadata.getOrDefault(
                ZoomCommunicationFieldNames.PaymentDetails.TOTAL_AMOUNT.name(), "")));

    return utrTicket.getId();
  }

  private TicketDTO getTicketDtoForUtrBankTransfer(
      Map<String, String> metadata, String s3Url, String utrNo, Long parentId) {
    GroupDTO group = getGroupForTicketing();
    return TicketDTO.builder()
        .entityId(utrNo)
        .parentId(parentId)
        .source(TicketSource.INTERNAL)
        .typeId(ZoomTicketingConstant.UTR_BANK_TRANSFER_TICKET_TYPE_ID)
        .subject(
            String.format(
                ZoomTicketingConstant.BANK_TRANSFER_MESSAGE,
                TicketEntityType.UTR.name(),
                "",
                metadata.getOrDefault(
                    ZoomCommunicationFieldNames.PaymentDetails.BANK_NAME.name(), ""),
                utrNo,
                s3Url,
                "see comments"))
        .title(
            String.format(
                ZoomTicketingConstant.BANK_TRANSFER_TICKET_TITLE,
                TicketEntityType.UTR.name(),
                utrNo))
        .assigneeId(group == null ? null : group.getId())
        .assigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP)
        .build();
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
}
