package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rivigo.finance.zoom.dto.ZoomClientCreditLimitBreachDTO;
import com.rivigo.riconet.core.dto.BankTransferRequestDTO;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.EpodApplicableDto;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.athenagps.AthenaGpsEventDto;
import com.rivigo.riconet.core.dto.client.ClientCodDodDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.zoom.backend.client.dto.request.ChequeBounceRequestDTO;
import com.rivigo.zoom.backend.client.dto.request.ZoomConsignmentUndeliveryDto;
import com.rivigo.zoom.billing.enums.ConsignmentLiability;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.dto.errorcorrection.ConsignmentQcDataSubmitDTO;
import java.util.Collection;
import java.util.List;

public interface ZoomBackendAPIClientService {

  void recalculateCpdOfBf(Long consignmentId);

  void unloadAssetCN(Long cnId);

  ClientDTO addClient(ClientDTO clientDTO);

  ClientDTO updateClient(ClientDTO clientDTO);

  ClientDTO deleteClient(Long id);

  OrganizationDTO addOrganization(OrganizationDTO orgDTO);

  OrganizationDTO updateOrganization(OrganizationDTO orgDTO);

  ConsignmentUploadedFilesDTO addInvoice(
      String invoiceUrl, String shortUrl, String cnote, Boolean isProForma);

  void handleWriteOffApproveRejectRequest(
      String cnote, WriteOffRequestAction writeOffRequestAction);

  Boolean handleConsignmentBlocker(ConsignmentBlockerRequestDTO consignmentBlockerRequestDTO);

  ClientCodDodDTO addVasDetails(ClientCodDodDTO clientCodDodDTO);

  ClientCodDodDTO updateVasDetails(ClientCodDodDTO clientCodDodDTO);

  Boolean deletePickup(Long pickupId);

  JsonNode addBusinessPartner(BusinessPartnerDTO businessPartnerDTO);

  JsonNode addFeederVendor(FeederVendorDTO feederVendorDTO);

  /**
   * This function is used to maked backend API call to reject bank transfer payment for given
   * consignment. <br>
   * MarkRecoveryPending flow is triggered for given consignment.
   *
   * @param chequeBounceRequestDTO Bank transfer payment details that were rejected.
   */
  JsonNode markRecoveryPending(ChequeBounceRequestDTO chequeBounceRequestDTO);

  JsonNode markRecoveryPendingBulk(List<ChequeBounceRequestDTO> chequeBounceRequestDTO);

  /**
   * This function is used to make Backend API call to knockoff bank transfer payment for given
   * cnote. <br>
   * This function ensures backward compatibility.
   *
   * @param cnote Cnote that needs to be knocked off.
   * @param bankTransferRequestDTO Bank transfer payment details to be knocked off.
   */
  void handleKnockOffRequestForCnote(String cnote, BankTransferRequestDTO bankTransferRequestDTO);

  void processVehicleEvent(PrimeEventDto primeEventDto, Long tripId);

  void processAthenaGpsEvent(AthenaGpsEventDto athenaGpsEventDto);

  void qcConsignmentV2(ConsignmentQcDataSubmitDTO dto);

  void markDelivered(String cnote);

  /**
   * Hits zoom backend API to generate final invoice for rivigo to pay CN's.
   *
   * @param cnote for generating invoice.
   */
  void generateInvoice(String cnote);

  /**
   * This function is used to update epod link.
   *
   * @param consignmentUploadedFilesDTO for uploading epod.
   * @return upload the s3 url for the epod in consignment_uploaded_files.
   */
  void uploadEpod(ConsignmentUploadedFilesDTO consignmentUploadedFilesDTO);

  /**
   * This function is used to update epod flag details.
   *
   * @param epodApplicableDTO for updating epod details.
   */
  void updateEpodDetails(EpodApplicableDto epodApplicableDTO);

  /**
   * Function used to make backend API call to start demurrage for given consignment on CN
   * undelivery.
   *
   * @param cnote contains cnote number of consignment.
   * @param startTime contains startTime of demurrage as string.
   * @param undeliveredCnRecordId contains undeliveredConsignment record id.
   */
  void startDemurrageOnCnUndelivery(String cnote, String startTime, String undeliveredCnRecordId);

  /**
   * Function used to make backend API call to start demurrage for given consignment on CN dispatch
   * or delivery hold.
   *
   * @param consignmentId consignment id.
   * @param consignmentAlertId consignment alert id, contains details regarding the
   *     dispatch/delivery hold.
   */
  void startDemurrageOnCnDispatchOrDeliveryHold(String consignmentId, String consignmentAlertId);

  /**
   * Function used to make backend API call to end demurrage for given consignment.
   *
   * @param cnote contains cnote number of consignment.
   */
  void endDemurrage(String cnote);

  /**
   * Function used to make backend API call to cancel ongoing demurrage for given consignment.
   *
   * @param cnote contains cnote number of consignment.
   */
  void cancelDemurrage(String cnote);

  /**
   * This function calls the blocking API in the zoom backend with the client code and
   * enable/disable flag.
   *
   * @param zoomClientCreditLimitBreachDto dto which contains client list and reason list of the
   *     blockers to be added.
   */
  void updateClientBlockerDetails(ZoomClientCreditLimitBreachDTO zoomClientCreditLimitBreachDto);

  /**
   * Hits Zoom Backend API to Update Consignment Liability
   *
   * @param consignmentId cn id
   * @param consignmentLiability Consignment Liability
   */
  void updateConsignmentLiability(Long consignmentId, ConsignmentLiability consignmentLiability);

  /**
   * Hits Backend API to retrigger CPD calculation for all affected CNs due to holiday creation or
   * updation.
   *
   * @param holidayV2Dto holiday details.
   */
  void retriggerCpdCalculationsForHoliday(HolidayV2Dto holidayV2Dto);

  /**
   * Function used to make backend API call to knockoff bank transfer payment for given UTR number.
   *
   * @param utrNo UTR number to be knocked off.
   */
  void knockOffUtrBankTransfer(String utrNo);

  /**
   * Function used to make backend API call to revert knockoff bank transfer payment for given UTR
   * number.
   *
   * @param utrNo UTR number to be revert knocked off.
   */
  void revertKnockOffUtrBankTransfer(String utrNo);

  /**
   * Method used to make backend API call to mark multiple consignments as undelivered.
   *
   * @param cnUndeliveryDtoList consignment undelivery details.
   */
  void undeliverMultipleConsignments(Collection<ZoomConsignmentUndeliveryDto> cnUndeliveryDtoList);
}
