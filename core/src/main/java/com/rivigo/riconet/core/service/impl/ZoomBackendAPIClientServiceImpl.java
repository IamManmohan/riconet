package com.rivigo.riconet.core.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.finance.zoom.dto.ZoomClientCreditLimitBreachDTO;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.constants.ConsignmentLiabilityParamConstants;
import com.rivigo.riconet.core.constants.UrlConstant;
import com.rivigo.riconet.core.dto.BankTransferRequestDTO;
import com.rivigo.riconet.core.dto.BusinessPartnerDTO;
import com.rivigo.riconet.core.dto.ChequeBounceDTO;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.ConsignmentUploadedFilesDTO;
import com.rivigo.riconet.core.dto.EpodApplicableDto;
import com.rivigo.riconet.core.dto.FeederVendorDTO;
import com.rivigo.riconet.core.dto.OrganizationDTO;
import com.rivigo.riconet.core.dto.PickupDeleteDtoV2;
import com.rivigo.riconet.core.dto.athenagps.AthenaGpsEventDto;
import com.rivigo.riconet.core.dto.client.ClientCodDodDTO;
import com.rivigo.riconet.core.dto.client.ClientDTO;
import com.rivigo.riconet.core.dto.primesync.PrimeEventDto;
import com.rivigo.riconet.core.enums.WriteOffRequestAction;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.zoom.billing.enums.ConsignmentLiability;
import com.rivigo.zoom.common.dto.HolidayV2Dto;
import com.rivigo.zoom.common.dto.errorcorrection.ConsignmentQcDataSubmitDTO;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import com.rivigo.zoom.util.rest.constants.ResponseJavaTypes;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
public class ZoomBackendAPIClientServiceImpl implements ZoomBackendAPIClientService {

  @Value("${zoom.url}")
  private String backendBaseUrl;

  @Autowired private ApiClientService apiClientService;

  @Autowired private ObjectMapper objectMapper;

  @Override
  public void handleWriteOffApproveRejectRequest(
      String cnote, WriteOffRequestAction writeOffRequestAction) {
    JsonNode responseJson;
    String url =
        UrlConstant.ZOOM_BACKEND_WRITE_OFF_REQUEST_ONBOARDING
            .replace("{cnote}", cnote)
            .replace("{requestAction}", writeOffRequestAction.name());
    try {
      responseJson = apiClientService.getEntity(null, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while handling Writeoff request with cnote: {} ", cnote, e);
      throw new ZoomException("Error while handling Writeoff request with cnote: %s", cnote);
    }
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }

  @Override
  public void unloadAssetCN(Long cnId) {
    String url = UrlConstant.ZOOM_BACKEND_ASSET_ONBOARDING.replace("{cnId}", cnId.toString());
    try {
      apiClientService.getEntity(null, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while triggering asset on-boarding with consignmentId: {}", cnId, e);
    }
  }

  @Override
  public void recalculateCpdOfBf(Long consignmentId) {
    JsonNode responseJson;
    MultiValueMap<String, String> valuesMap = new LinkedMultiValueMap<>();
    valuesMap.put("consignmentId", Collections.singletonList(consignmentId.toString()));
    String url = UrlConstant.ZOOM_BACKEND_BF_CPD_CALCULATION;
    try {
      responseJson =
          apiClientService.getEntity(null, HttpMethod.PUT, url, valuesMap, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while recalculating cpd of BF consignment with id: {} ", consignmentId, e);
      throw new ZoomException(
          "Error while recalculating cpd of BF consignment with id: " + consignmentId);
    }
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }

  @Override
  public ClientDTO addClient(ClientDTO clientDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE;
    log.info("Adding client {}", clientDTO);
    try {
      responseJson =
          apiClientService.getEntity(clientDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while creating Client {} , {}", clientDTO, e);
      throw new ZoomException("Error while creating Client {}" + clientDTO);
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(ClientDTO.class));
  }

  @Override
  public ClientDTO updateClient(ClientDTO clientDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE;
    log.info("Updating client {}", clientDTO);
    try {
      responseJson =
          apiClientService.getEntity(clientDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Client {} , {}", clientDTO, e);
      throw new ZoomException("Error while updating Client {}" + clientDTO);
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(ClientDTO.class));
  }

  @Override
  public ClientDTO deleteClient(Long id) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CLIENT_SERVICE + "/" + id.toString();
    log.info("Deleting client with id {}", id);
    try {
      responseJson = apiClientService.getEntity(null, HttpMethod.DELETE, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while deleting Client with Id {} , {}", id, e);
      throw new ZoomException("Error while deleting Client with Id {}" + id);
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(ClientDTO.class));
  }

  @Override
  public ConsignmentUploadedFilesDTO addInvoice(
      String invoiceUrl, String shortUrl, String cnote, Boolean isProForma) {
    JsonNode responseJson;
    Map<String, String> data = new HashMap<>();
    data.put(ConsignmentConstant.SHORT_URL, shortUrl);
    data.put(ConsignmentConstant.CNOTE, cnote);
    data.put(ConsignmentConstant.URL, invoiceUrl);
    data.put(ConsignmentConstant.IS_PRO_FORMA, isProForma.toString());
    String url = UrlConstant.ZOOM_BACKEND_CONSIGNMENT_INVOICE;
    log.info("Updating invoice {} for cnote {}", shortUrl, cnote);
    try {
      responseJson = apiClientService.getEntity(data, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Invoice {} for Cnote {} , {}", shortUrl, cnote, e);
      throw new ZoomException("Error while updating Invoice for cnote :" + cnote);
    }
    try {
      return apiClientService.parseNewResponseJsonNode(
          responseJson, objectMapper.constructType(ConsignmentUploadedFilesDTO.class));
    } catch (Exception e) {
      log.error("Error converting json in dto {}", e.getMessage(), e);
      return null;
    }
  }

  @Override
  public OrganizationDTO addOrganization(OrganizationDTO orgDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_ORGANIZATION_SERVICE;
    log.info("Adding organization {}", orgDTO);
    try {
      responseJson = apiClientService.getEntity(orgDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while creating Organization {} , {}", orgDTO, e);
      throw new ZoomException("Error while creating Organization {}" + orgDTO);
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(OrganizationDTO.class));
  }

  @Override
  public OrganizationDTO updateOrganization(OrganizationDTO orgDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_ORGANIZATION_SERVICE;
    log.info("Updating organization {}", orgDTO);
    try {
      responseJson = apiClientService.getEntity(orgDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating Organization {} , {}", orgDTO, e);
      throw new ZoomException("Error while updating Organization {}" + orgDTO);
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(OrganizationDTO.class));
  }

  @Override
  public ClientCodDodDTO addVasDetails(ClientCodDodDTO clientCodDodDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_VAS_DETAILS_SERVICE;
    log.info("Adding vas details {}", clientCodDodDTO.toString());
    try {
      responseJson =
          apiClientService.getEntity(clientCodDodDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while adding vas details {} ", clientCodDodDTO.toString(), e);
      throw new ZoomException("Error while adding vas details " + clientCodDodDTO.toString());
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(ClientCodDodDTO.class));
  }

  @Override
  public ClientCodDodDTO updateVasDetails(ClientCodDodDTO clientCodDodDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_VAS_DETAILS_SERVICE;
    log.info("Updating vas details {}", clientCodDodDTO.toString());
    try {
      responseJson =
          apiClientService.getEntity(clientCodDodDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while updating vas details {} ", clientCodDodDTO, e);
      throw new ZoomException("Error while updating Vas Details" + clientCodDodDTO.toString());
    }
    return apiClientService.parseNewResponseJsonNode(
        responseJson, objectMapper.constructType(ClientCodDodDTO.class));
  }

  @Override
  public Boolean deletePickup(Long pickupId) {
    PickupDeleteDtoV2 pickupDeleteDto = new PickupDeleteDtoV2();
    pickupDeleteDto.setId(pickupId);
    JsonNode responseJson;
    log.info(" Cancelling pickup with pickup delete dto {}", pickupDeleteDto);
    try {
      responseJson =
          apiClientService.getEntity(
              pickupDeleteDto,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_CANCEL_PICKUP,
              null,
              backendBaseUrl);

    } catch (IOException e) {
      log.error("Error while cancelling pickup with pickupDeleteDto {}", pickupDeleteDto);
      throw new ZoomException(
          "Error while cancelling pickup with pickupdelete dto %s", pickupDeleteDto);
    }
    return apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
  }

  public Boolean handleConsignmentBlocker(
      ConsignmentBlockerRequestDTO consignmentBlockerRequestDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_CONSIGNMENT_BLOCKER;
    log.info("Handle consignmentBlocker {}", consignmentBlockerRequestDTO);
    try {
      responseJson =
          apiClientService.getEntity(
              consignmentBlockerRequestDTO, HttpMethod.POST, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while handling consignmentBlocker {}", consignmentBlockerRequestDTO, e);
      throw new ZoomException(
          "Error while handling consignmentBlocker %s", consignmentBlockerRequestDTO);
    }
    return apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
  }

  @Override
  public JsonNode addBusinessPartner(BusinessPartnerDTO businessPartnerDTO) {
    JsonNode responseJson;
    log.info(" Creating a new vendor with dto {}", businessPartnerDTO);
    try {
      responseJson =
          apiClientService.getEntity(
              businessPartnerDTO,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_CREATE_BP + "?" + "isvalidationRequired=false",
              null,
              backendBaseUrl);
      log.info("Business Partner Created {}", responseJson);
      return responseJson;

    } catch (IOException e) {
      log.error("Error while creating BP with dto {}", businessPartnerDTO);
      throw new ZoomException("Error while creating BP with dto %s", businessPartnerDTO);
    }
  }

  public JsonNode addFeederVendor(FeederVendorDTO feederVendorDTO) {
    JsonNode responseJson;
    log.info(" Creating a new vendor with dto {}", feederVendorDTO);
    try {
      responseJson =
          apiClientService.getEntity(
              feederVendorDTO,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_CREATE_VENDOR,
              null,
              backendBaseUrl);
      log.info("Feeder Vendor Created {}", responseJson);
      return responseJson;
    } catch (IOException e) {
      log.error("Error while creating vendor with dto {}", feederVendorDTO);
      throw new ZoomException("Error while creating vendor with dto %s", feederVendorDTO);
    }
  }

  /**
   * This function is used to maked backend API call to reject bank transfer payment for given
   * consignment. <br>
   * MarkRecoveryPending flow is triggered for given consignment.
   *
   * @param chequeBounceDTO Bank transfer payment details that were rejected.
   */
  @Override
  public JsonNode markRecoveryPending(ChequeBounceDTO chequeBounceDTO) {
    return markRecoveryPendingInternal(
        chequeBounceDTO, UrlConstant.ZOOM_BACKEND_MARK_HANDOVER_AS_RECOVERY_PENDING);
  }

  @Override
  public JsonNode markRecoveryPendingBulk(List<ChequeBounceDTO> chequeBounceDTO) {
    return markRecoveryPendingInternal(
        chequeBounceDTO, UrlConstant.ZOOM_BACKEND_MARK_HANDOVER_AS_RECOVERY_PENDING_BULK);
  }

  private <T> JsonNode markRecoveryPendingInternal(
      T chequeBounceDTO, String recoveryPendingEndpoint) {
    log.info("Mark Recovery Pending With {} ", chequeBounceDTO);
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              chequeBounceDTO, HttpMethod.PUT, recoveryPendingEndpoint, null, backendBaseUrl);
      log.debug("response {}", responseJson);
    } catch (IOException e) {
      log.error(
          "Exception occurred while marking recovery pending for cn in zoom tech: {} ",
          chequeBounceDTO,
          e);
      throw new ZoomException(
          "Exception occurred while marking recovery pending for cn in zoom tech : %s",
          chequeBounceDTO);
    }
    return apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.JSON_NODE);
  }

  /**
   * This function is used to make Backend API call to knockoff bank transfer payment for given
   * cnote. <br>
   * This function ensures backward compatibility.
   *
   * @param cnote Cnote that needs to be knocked off.
   * @param bankTransferRequestDTO Bank transfer payment details to be knocked off.
   */
  @Override
  public void handleKnockOffRequestForCnote(
      String cnote, @Valid BankTransferRequestDTO bankTransferRequestDTO) {
    JsonNode responseJson;
    String url = UrlConstant.ZOOM_BACKEND_KNOCK_OFF_REQUEST.replace("{cnote}", cnote);
    try {
      responseJson =
          apiClientService.getEntity(
              bankTransferRequestDTO, HttpMethod.PUT, url, null, backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while handling Knock off request with cnote: {} ", cnote, e);
      throw new ZoomException("Error while handling Knock off request with cnote: %s", cnote);
    }
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }

  @Override
  public void processVehicleEvent(PrimeEventDto primeEventDto, Long tripId) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              primeEventDto,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_PROCESS_VEHICLE_EVENT.replace("{tripId}", tripId.toString()),
              null,
              backendBaseUrl);
      Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        throw new ZoomException("Error in processing vehicle event");
      }
    } catch (IOException e) {
      log.error("Error while processing event with dto {}, trip id: {}", primeEventDto, tripId);
      throw new ZoomException("Error while processing event for trip id: %s", tripId);
    }
  }

  @Override
  public void processAthenaGpsEvent(AthenaGpsEventDto athenaGpsEventDto) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              athenaGpsEventDto,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_PROCESS_ATHENA_GPS_EVENT,
              null,
              backendBaseUrl);
      apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
    } catch (IOException e) {
      log.error("Error while processing event with dto {}", athenaGpsEventDto);
      throw new ZoomException("Error while processing event for gps");
    }
  }

  /**
   * Zoom Backend API to submit QC data.
   *
   * @param dto qc data submit dto
   */
  @Override
  public void qcConsignmentV2(ConsignmentQcDataSubmitDTO dto) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              dto,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_QC_CONSIGNMENT_V2,
              null,
              backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while handling qcConsignmentV2 ", e);
      throw new ZoomException(
          "Error while handling qcConsignmentV2 for cnote : %s", dto.getCnote());
    }
    // Calling parse json node to verify that response status is SUCCESS or throw exception
    // otherwise.
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }

  @Override
  public void markDelivered(String cnote) {
    log.info("Marking cnote {} as delivered", cnote);
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_MARK_DELIVERED.replace("{cnote}", cnote),
              null,
              backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while marking Zoom Doc CN as Delivered ", e);
      throw new ZoomException("Error while marking Zoom Doc Delivered for cnId : %s", cnote);
    }
    // Calling parse json node to verify that response status is SUCCESS or throw exception
    // otherwise.
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }

  /**
   * Hits zoom backend API to generate final invoice for rivigo to pay CN's.
   *
   * @param cnote for generating invoice.
   */
  @Override
  public void generateInvoice(String cnote) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.set("cnote", cnote);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_GENERATE_INVOICE,
              queryParams,
              backendBaseUrl);
    } catch (IOException e) {
      log.error("Error while generating invoice for cnote {}", cnote, e);
      throw new ZoomException("Error while generating invoice for cnote : %s", cnote);
    }
    // Calling parse json node to verify that response status is SUCCESS or throw exception
    // otherwise.
    apiClientService.parseNewResponseJsonNode(responseJson, null);
  }
  /**
   * function that uploads the e-pod by hitting the zoom-backend api.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Override
  public void uploadEpod(ConsignmentUploadedFilesDTO consignmentUploadedFilesDTO) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              consignmentUploadedFilesDTO,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_UPLOAD_EPOD,
              null,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        throw new ZoomException("Error in uploading epod");
      }
    } catch (IOException e) {
      throw new ZoomException("Error while uploading epod ", e);
    }
  }

  /**
   * function that hits the zoom-backend upload epod api so that epod is stored in consignment
   * uploaded files.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  @Override
  public void updateEpodDetails(EpodApplicableDto epodApplicableDTO) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              epodApplicableDTO,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_UPDATE_EPOD_FLAG,
              null,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        throw new ZoomException("Error in updating epod flag");
      }
    } catch (IOException e) {
      throw new ZoomException("Error while updating epod flag ", e);
    }
  }

  /**
   * Function used to make backend API call to start demurrage for given consignment.
   *
   * @param cnote contains cnote number of consignment.
   * @param startTime contains startTime of demurrage as string.
   * @param undeliveredCnRecordId contains undeliveredConsignment record id.
   */
  @Override
  public void startDemurrage(String cnote, String startTime, String undeliveredCnRecordId) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.add(ConsignmentConstant.CNOTE, cnote);
      queryParams.add("startTime", startTime);
      queryParams.add("undeliveredConsignmentRecordId", undeliveredCnRecordId);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.POST,
              UrlConstant.ZOOM_BACKEND_START_DEMURRAGE,
              queryParams,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.error("Cnote {} not valid for start demurrage request at time {}.", cnote, startTime);
      }
    } catch (IOException e) {
      throw new ZoomException(
          "Error while starting demurrage for cnote {} at time {}", cnote, startTime, e);
    }
  }

  /**
   * Function used to make backend API call to end demurrage for given consignment.
   *
   * @param cnote contains cnote number of consignment.
   */
  @Override
  public void endDemurrage(String cnote) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.set(ConsignmentConstant.CNOTE, cnote);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_END_DEMURRAGE,
              queryParams,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.error("Cnote {} not valid for end demurrage request.", cnote);
      }
    } catch (IOException e) {
      throw new ZoomException("Error while ending demurrage for cnote {}", cnote, e);
    }
  }

  /**
   * Function used to make backend API call to cancel ongoing demurrage for given consignment.
   *
   * @param cnote contains cnote number of consignment.
   */
  @Override
  public void cancelDemurrage(String cnote) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.set(ConsignmentConstant.CNOTE, cnote);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_CANCEL_DEMURRAGE,
              queryParams,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.error("Cnote {} not valid for cancel demurrage request.", cnote);
      }
    } catch (IOException e) {
      throw new ZoomException("Error while cancelling demurrage for cnote {}", cnote, e);
    }
  }
  /**
   * This function calls the blocking API in the zoom backend with the client code and
   * enable/disable flag.
   *
   * @param zoomClientCreditLimitBreachDto dto which contains client list and reason list of the
   *     blockers to be added.
   */
  @Override
  public void updateClientBlockerDetails(
      @NonNull ZoomClientCreditLimitBreachDTO zoomClientCreditLimitBreachDto) {
    try {
      final JsonNode responseJson =
          apiClientService.getEntity(
              zoomClientCreditLimitBreachDto,
              HttpMethod.POST,
              UrlConstant.BLOCK_UNBLOCK_CLIENT,
              null,
              backendBaseUrl);
      log.info(
          "client blocker request received from compass with dto {} and response from backend {}",
          zoomClientCreditLimitBreachDto,
          responseJson);
    } catch (IOException e) {
      throw new ZoomException("Error while updating client blocker ", e);
    }
  }

  /**
   * Hits Zoom Backend API to Update Consignment Liability
   *
   * @param consignmentId cn id
   * @param consignmentLiability Consignment Liability
   */
  @Override
  public void updateConsignmentLiability(
      Long consignmentId, ConsignmentLiability consignmentLiability) {
    JsonNode responseJson;
    try {
      MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
      paramMap.set(ConsignmentLiabilityParamConstants.CONSIGNMENT_ID, consignmentId.toString());
      paramMap.set(
          ConsignmentLiabilityParamConstants.CONSIGNMENT_LIABILITY, consignmentLiability.name());
      log.info("Attempting liability update with params {}", paramMap);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_UPDATE_CONSIGNMENT_LIABILITY,
              paramMap,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        throw new ZoomException("Error in updating consignment liability");
      }
    } catch (IOException e) {
      throw new ZoomException("Error while updating consignment liability ", e);
    }
  }

  /**
   * Hits Backend API to retrigger CPD calculation for all affected CNs due to holiday creation or
   * updation.
   *
   * @param holidayV2Dto holiday details.
   */
  @Override
  public void retriggerCpdCalculationsForHoliday(@NonNull HolidayV2Dto holidayV2Dto) {
    JsonNode responseJson;
    try {
      responseJson =
          apiClientService.getEntity(
              holidayV2Dto,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_TRIGGER_CPD_CALCULATIONS_HOLIDAY,
              null,
              backendBaseUrl);
      Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.info("Error in triggering CPD calculations for Holiday update.");
      }
    } catch (IOException e) {
      throw new ZoomException("Error in triggering CPD calculations for Holiday update.", e);
    }
  }

  /**
   * Function used to make backend API call to knockoff bank transfer payment for given UTR number.
   *
   * @param utrNo UTR number to be knocked off.
   */
  @Override
  public void knockOffUtrBankTransfer(String utrNo) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.set("utrNo", utrNo);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_KNOCKOFF_COMPLETE_UTR_BANK_TRANSFER,
              queryParams,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.error("Knockoff request for UTR: {} failed.", utrNo);
      }
    } catch (IOException e) {
      throw new ZoomException("Error while knocking off bank transfer for UTR {}", utrNo, e);
    }
  }

  /**
   * Function used to make backend API call to revert knockoff bank transfer payment for given UTR
   * number.
   *
   * @param utrNo UTR number to be revert knocked off.
   */
  @Override
  public void revertKnockOffUtrBankTransfer(String utrNo) {
    JsonNode responseJson;
    try {
      final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.set("utrNo", utrNo);
      responseJson =
          apiClientService.getEntity(
              null,
              HttpMethod.PUT,
              UrlConstant.ZOOM_BACKEND_REVERT_KNOCKOFF_UTR_BANK_TRANSFER,
              queryParams,
              backendBaseUrl);
      final Boolean isSuccess =
          apiClientService.parseNewResponseJsonNode(responseJson, ResponseJavaTypes.BOOLEAN);
      if (!Boolean.TRUE.equals(isSuccess)) {
        log.error("Revert Knockoff request for UTR: {} failed.", utrNo);
      }
    } catch (IOException e) {
      throw new ZoomException("Error while Reverting Knockoff bank transfer for UTR {}", utrNo, e);
    }
  }
}
