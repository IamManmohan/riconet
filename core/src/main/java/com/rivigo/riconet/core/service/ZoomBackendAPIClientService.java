package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.client.ClientCodDodDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.zoom.common.enums.PriorityReasonType;

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

  void handleApproveRejectRequest(String cnote, WriteOffRequestAction writeOffRequestAction);

  Boolean handleConsignmentBlocker(ConsignmentBlockerRequestDTO consignmentBlockerRequestDTO);

  void setPriorityMapping(String cnote, PriorityReasonType reason);

  ClientCodDodDTO addVasDetails(ClientCodDodDTO clientCodDodDTO);

  ClientCodDodDTO updateVasDetails(ClientCodDodDTO clientCodDodDTO);

  Boolean deletePickup(Long pickupId);
}
