package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;

public interface ZoomBackendAPIClientService {

  void updateQcCheck(Long consignmentId, Boolean qcCheck);

  void recalculateCpdOfBf(Long consignmentId);

  void triggerPolicyGeneration(Long consignmentId);

  ClientDTO addClient(ClientDTO clientDTO);

  ClientDTO updateClient(ClientDTO clientDTO);

  ClientDTO deleteClient(Long id);

  OrganizationDTO addOrganization(OrganizationDTO orgDTO);

  OrganizationDTO updateOrganization(OrganizationDTO orgDTO);

  ConsignmentUploadedFilesDTO addInvoice(String invoiceUrl, String shortUrl, String cnote);

  void setPriorityMapping(String cnote);
}
