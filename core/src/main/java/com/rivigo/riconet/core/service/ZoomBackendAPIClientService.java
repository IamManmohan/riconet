package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.riconet.core.dto.BankTransferRequestDTO;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.ChequeBounceDTO;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.client.ClientCodDodDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.zoom.common.enums.PriorityReasonType;
import org.springframework.http.HttpMethod;

public interface ZoomBackendAPIClientService {

  void updateQcCheck(Long consignmentId, Boolean qcCheck);

  void recalculateCpdOfBf(Long consignmentId);

  void unloadAssetCN(Long cnId);

  ClientDTO addClient(ClientDTO clientDTO);

  ClientDTO updateClient(ClientDTO clientDTO);

  ClientDTO deleteClient(Long id);

  OrganizationDTO addOrganization(OrganizationDTO orgDTO);

  OrganizationDTO updateOrganization(OrganizationDTO orgDTO);

  ConsignmentUploadedFilesDTO addInvoice(String invoiceUrl, String shortUrl, String cnote);

  void handleQcBlockerClosure(Long ticketId);

  void handleWriteOffApproveRejectRequest(
      String cnote, WriteOffRequestAction writeOffRequestAction);

  Boolean handleConsignmentBlocker(ConsignmentBlockerRequestDTO consignmentBlockerRequestDTO);

  void setPriorityMapping(String cnote, PriorityReasonType reason);

  ClientCodDodDTO addVasDetails(ClientCodDodDTO clientCodDodDTO);

  ClientCodDodDTO updateVasDetails(ClientCodDodDTO clientCodDodDTO);

  Boolean deletePickup(Long pickupId);

  JsonNode addBusinessPartner(BusinessPartnerDTO businessPartnerDTO, HttpMethod httpMethod);

  JsonNode addFeederVendor(FeederVendorDTO feederVendorDTO);

  JsonNode markRecoveryPending(ChequeBounceDTO chequeBounceDTO);

  void handleKnockOffRequest(String cnote, BankTransferRequestDTO bankTransferRequestDTO);
}
