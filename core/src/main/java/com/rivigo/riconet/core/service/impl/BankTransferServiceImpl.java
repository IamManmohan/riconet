package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketSource;
import com.rivigo.riconet.core.service.BankTransferService;
import com.rivigo.riconet.core.service.ConsignmentReadOnlyService;
import com.rivigo.riconet.core.service.UploadedFileRecordService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.zoom.common.enums.EntityType;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.UploadedFileRecord;
import com.rivigo.zoom.exceptions.ZoomException;
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

  private final ObjectMapper objectMapper;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  private final ConsignmentReadOnlyService consignmentReadOnlyService;

  private final UploadedFileRecordService uploadedFileRecordService;

  @Override
  public void createBankTransferTicket(Map<String, String> metadata) {
    PaymentDetailV2 paymentDetailV2 = objectMapper.convertValue(metadata, PaymentDetailV2.class);
    ConsignmentReadOnly consignment =
        consignmentReadOnlyService
            .findConsignmentById(paymentDetailV2.getConsignmentId())
            .orElseThrow(() -> new ZoomException("No consignment found with given id"));
    String s3Url = getS3Url(consignment);
    zoomTicketingAPIClientService.createTicket(
        getTicketDTOForBankTransfer(consignment.getCnote(), paymentDetailV2, s3Url));
  }

  private String getS3Url(ConsignmentReadOnly consignment) {
    List<UploadedFileRecord> uploadedFiles =
        uploadedFileRecordService.getUploadedFileRecordByEntityAndFileType(
            EntityType.CONSIGNMENT, consignment.getCnote(), FileTypes.BANK_TRANSFER);
    if (CollectionUtils.isEmpty(uploadedFiles)) {
      uploadedFiles =
          uploadedFileRecordService.getUploadedFileRecordByEntityAndFileType(
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
   * @param paymentDetails paymentdetails with bankname, utr no, transferredAmount
   * @param s3URL s3 url
   * @return TicketDto
   */
  private TicketDTO getTicketDTOForBankTransfer(
      @NotNull String cnote, PaymentDetailV2 paymentDetails, String s3URL) {
    GroupDTO group =
        zoomTicketingAPIClientService.getGroupId(
            ZoomTicketingConstant.HQTR_LOCATION_ID,
            ZoomTicketingConstant.BANK_TRANSFER_GROUP_NAME,
            LocationType.HQTR);
    return TicketDTO.builder()
        .entityId(cnote)
        .source(TicketSource.INTERNAL)
        .typeId(ZoomTicketingConstant.BANK_TRANSFER_TYPE_ID)
        .subject(
            String.format(
                ZoomTicketingConstant.BANK_TRANSFER_MESSAGE,
                cnote,
                paymentDetails.getBankName(),
                paymentDetails.getTransactionReferenceNo(),
                s3URL,
                paymentDetails.getTransferredAmount(),
                paymentDetails.getTotalAmount()))
        .title(String.format(ZoomTicketingConstant.BANK_TRANSFER_TICKET_TITLE, cnote))
        .assigneeId(group == null ? null : group.getId())
        .assigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP)
        .build();
  }
}
