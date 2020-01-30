package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.RIVIGO_ORGANIZATION_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.InvoiceDocumentPreparedDTO;
import com.rivigo.riconet.core.service.ConsignmentInvoiceService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.UrlShortnerService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.repository.mysql.PaymentDetailV2Repository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 20/6/18. */
@Slf4j
@Service
public class ConsignmentInvoiceServiceImpl implements ConsignmentInvoiceService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private UrlShortnerService urlShortnerService;

  @Autowired private SmsService smsService;

  @Autowired private PaymentDetailV2Repository paymentDetailV2Repository;

  @Autowired private ConsignmentService consignmentService;

  @Override
  public void saveInvoiceDetails(String dto) {
    InvoiceDocumentPreparedDTO invoiceDocumentPreparedDTO = getInvoicePreparedDTO(dto);
    if (invoiceDocumentPreparedDTO == null) {
      return;
    }
    Consignment consignment =
        consignmentService.getConsignmentByCnote(invoiceDocumentPreparedDTO.getCnote());
    if (consignment == null) {
      log.error("Consignment doesn't exist {}", invoiceDocumentPreparedDTO);
      throw new ZoomException(
          "Consignment doesn't exist for cnote" + invoiceDocumentPreparedDTO.getCnote());
    }
    if (RIVIGO_ORGANIZATION_ID != consignment.getOrganizationId()) {
      return;
    }
    String shortUrl = urlShortnerService.shortenUrl(invoiceDocumentPreparedDTO.getEncodedUrl());
    zoomBackendAPIClientService.addInvoice(
        invoiceDocumentPreparedDTO.getEncodedUrl(),
        shortUrl,
        invoiceDocumentPreparedDTO.getCnote());
    if (!StringUtils.isBlank(shortUrl)) {
      String message =
          "New CN "
              + invoiceDocumentPreparedDTO.getCnote()
              + " successfully created, click here to access GST invoice: "
              + shortUrl;

      PaymentDetailV2 paymentDetailV2 =
          paymentDetailV2Repository.findByConsignmentIdAndIsActive(consignment.getId(), true);
      String mobileNumber =
          PaymentMode.PAID.equals(paymentDetailV2.getPaymentMode())
              ? consignment.getConsignorPhone()
              : consignment.getConsigneePhone();
      log.info("Sending invoice related sms {} to mobileNumber {}", message, mobileNumber);

      smsService.sendSms(mobileNumber, message);
    }
  }

  private InvoiceDocumentPreparedDTO getInvoicePreparedDTO(String dtoString) {
    InvoiceDocumentPreparedDTO invoiceDocumentPreparedDTO = null;
    try {
      invoiceDocumentPreparedDTO =
          objectMapper.readValue(dtoString, InvoiceDocumentPreparedDTO.class);
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", dtoString, ex);
      return null;
    }
    return invoiceDocumentPreparedDTO;
  }
}
