package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.finance.zoom.dto.EventPayload;
import com.rivigo.finance.zoom.enums.ZoomEventType;
import com.rivigo.riconet.core.service.ClientMasterService;
import com.rivigo.riconet.core.service.ConsignmentInvoiceService;
import com.rivigo.riconet.core.service.ConsignmentLiabilityService;
import com.rivigo.riconet.core.service.EpodService;
import com.rivigo.riconet.core.service.FeederVendorService;
import com.rivigo.riconet.core.service.FinanceEventService;
import com.rivigo.riconet.core.service.HandoverCollectionService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ZoomPropertyName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by ashfakh on 7/6/18. */
@Service
@Slf4j
public class FinanceEventServiceImpl implements FinanceEventService {

  @Autowired private ClientMasterService clientMasterService;

  @Autowired private FeederVendorService feederVendorService;

  @Autowired private ConsignmentInvoiceService consignmentInvoiceService;

  @Autowired private HandoverCollectionService handoverCollectionService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ConsignmentLiabilityService consignmentLiabilityService;

  /**
   * This service is used for uploading epod link.
   *
   * @param json string which is converted to consignmentUploadedFilesDTO for further api calls.
   * @return upload the s3 url for the epod in consignment_uploaded_files.
   */
  @Autowired private EpodService epodService;

  @Override
  public void processFinanceEvents(EventPayload eventPayload) {
    ZoomEventType eventType = eventPayload.getEventType();
    switch (eventType) {
      case CMS_CLIENT_UPSERT:
        clientMasterService.createUpdateClient(eventPayload.getPayload());
        break;
      case CMS_CLIENT_SETTING_UPSERT:
        clientMasterService.updateEpodDetails(eventPayload.getPayload());
        break;
      case ELECTRONIC_POD_PREPARED:
        epodService.uploadEpod(eventPayload.getPayload());
        break;
      case INVOICE_DOCUMENT_PREPARED:
        consignmentInvoiceService.saveInvoiceDetails(eventPayload.getPayload());
        break;
      case VENDOR_ACTIVE_EVENT:
        // flag to enable or disable vendor onboarding
        if (zoomPropertyService.getBoolean(ZoomPropertyName.IS_VENDOR_ONBOARDING_ENABLED, false)) {
          JsonNode response = feederVendorService.createFeederVendor(eventPayload.getPayload());
          log.info("Vendor created {}", response);
        }
        break;
      case HANDOVER_COLLECTION_POST:
      case HANDOVER_COLLECTION_UNPOST:
        handoverCollectionService.handleHandoverCollectionPostUnpostEvent(
            eventPayload.getPayload(), eventType);
        break;
      case HANDOVER_COLLECTION_EXCLUDE:
        handoverCollectionService.handleHandoverCollectionExcludeEvent(
            eventPayload.getPayload(), eventType);
        break;
      case CONSIGNMENT_LIABILITY_UPDATE:
        consignmentLiabilityService.updateConsignmentLiability(eventPayload.getPayload());
      default:
        log.info("Event does not trigger anything {}", eventType);
    }
  }
}
