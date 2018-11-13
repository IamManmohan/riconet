package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.RIVIGO_ORGANIZATION_ID;
import static com.rivigo.riconet.core.constants.ReasonConstant.QC_BLOCKER_REASON;
import static com.rivigo.riconet.core.constants.ReasonConstant.QC_BLOCKER_SUB_REASON;
import static com.rivigo.riconet.core.predicates.TicketPredicate.isOpenQcTicket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.EmailConstant;
import com.rivigo.riconet.core.constants.ReasonConstant;
import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.dto.ConsignmentBlockerRequestDTO;
import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketCommentDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketSource;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.ConsignmentCodDodService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.PincodeService;
import com.rivigo.riconet.core.service.QcService;
import com.rivigo.riconet.core.service.SmsService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomBillingAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.ruleengine.QCRuleEngine;
import com.rivigo.riconet.ruleengine.constants.RuleEngineVariableNameConstant;
import com.rivigo.zoom.common.dto.client.ClientClusterMetadataDTO;
import com.rivigo.zoom.common.dto.client.ClientPincodeMetadataDTO;
import com.rivigo.zoom.common.enums.ClientEntityType;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.ConsignmentBlockerRequestType;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.LocationTag;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.QcType;
import com.rivigo.zoom.common.model.ClientEntityMetadata;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentCodDod;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.PinCode;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.model.redis.QcBlockerActionParams;
import com.rivigo.zoom.common.repository.redis.QcBlockerActionParamsRedisRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class QcServiceImpl implements QcService {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Autowired private SmsService smsService;

  @Autowired private QCRuleEngine qcRuleEngine;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ConsignmentCodDodService consignmentCodDodService;

  @Autowired private ClientEntityMetadataServiceImpl clientEntityMetadataService;

  @Autowired private PincodeService pincodeService;

  @Autowired private AdministrativeEntityService administrativeEntityService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private LocationService locationService;

  @Autowired private ZoomBillingAPIClientService zoomBillingAPIClientService;

  @Autowired private EmailService emailService;

  @Autowired private UserMasterService userMasterService;

  @Autowired private OrganizationService organizationService;

  @Autowired private QcBlockerActionParamsRedisRepository qcBlockerActionParamsRedisRepository;

  @Autowired private TicketingService ticketingService;

  public void consumeLoadingEvent(ConsignmentBasicDTO loadingData) {
    if (ConsignmentStatus.DELIVERY_PLANNED.equals(loadingData.getStatus())) {
      return;
    }
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService
            .getTicketsByCnoteAndType(loadingData.getCnote(), getQcTicketTypes())
            .stream()
            .filter(ticketDTO -> isOpenQcTicket().test(ticketDTO))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(ticketList)) {
      return;
    }
    Location location = locationService.getLocationById(loadingData.getLocationId());
    Long toLocationId =
        consignmentScheduleService
            .getActivePlan(loadingData.getConsignmentId())
            .stream()
            .filter(cs -> cs.getLocationType() == LocationTypeV2.LOCATION)
            .max(Comparator.comparing(ConsignmentSchedule::getSequence))
            .map(ConsignmentSchedule::getLocationId)
            .orElse(null);
    log.debug("Consignment is to be delivered from {}", toLocationId);
    ticketList.forEach(
        ticketDTO -> {
          if ((ZoomTicketingConstant.QC_RECHECK_TYPE_ID.equals(ticketDTO.getTypeId())
                  || location.getOrganizationId() != RIVIGO_ORGANIZATION_ID
                  || !com.rivigo.zoom.common.enums.LocationType.PROCESSING_CENTER.equals(
                      location.getLocationType()))
              && !location.getId().equals(toLocationId)) {
            ticketDTO.setAssigneeId(null);
            ticketDTO.setAssigneeType(AssigneeType.NONE);
            zoomTicketingAPIClientService.editTicket(ticketDTO);
          } else {
            closeTicket(ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_DISPATCH);
            zoomBackendAPIClientService.updateQcCheck(loadingData.getConsignmentId(), false);
          }
        });
  }

  private void closeTicket(TicketDTO ticketDTO, String reasonOfClosure) {
    if (TicketStatus.NEW.equals(ticketDTO.getStatus())) {
      ticketDTO.setStatus(TicketStatus.IN_PROGRESS);
      zoomTicketingAPIClientService.editTicket(ticketDTO);
    }
    ticketDTO.setReasonOfClosure(reasonOfClosure);
    ticketDTO.setStatus(TicketStatus.CLOSED);
    zoomTicketingAPIClientService.editTicket(ticketDTO);
  }

  public void consumeUnloadingEvent(ConsignmentBasicDTO unloadingData) {
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService
            .getTicketsByCnoteAndType(unloadingData.getCnote(), getQcTicketTypes())
            .stream()
            .filter(ticketDTO -> isOpenQcTicket().test(ticketDTO))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(ticketList)) {
      return;
    }
    AdministrativeEntity currentCluster =
        administrativeEntityService.findParentCluster(unloadingData.getLocationId());
    AdministrativeEntity fromCluster =
        administrativeEntityService.findParentCluster(unloadingData.getFromId());
    if ((!fromCluster.getCode().equals(currentCluster.getCode()))) {
      closeQcTickets(
          ticketList,
          unloadingData.getConsignmentId(),
          ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_DISPATCH);
      return;
    }
    GroupDTO group =
        zoomTicketingAPIClientService.getGroupId(
            unloadingData.getLocationId(), ZoomTicketingConstant.QC_GROUP_NAME, LocationType.OU);
    ticketList.forEach(
        ticketDTO -> {
          ticketDTO.setAssigneeId(group == null ? null : group.getId());
          ticketDTO.setAssigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP);
          zoomTicketingAPIClientService.editTicket(ticketDTO);
          if (ticketDTO.getTypeId().equals(ZoomTicketingConstant.QC_RECHECK_TYPE_ID)
              && !TicketStatus.CLOSED.equals(ticketDTO.getStatus())) {
            placeValidationBlockerForBFCNsAtFirstRivigoLocation(
                unloadingData.getConsignmentId(), unloadingData.getLocationId());
          }
        });
    if (group != null) {
      return;
    }
    Location location = locationService.getLocationById(unloadingData.getLocationId());
    if (!location.getOrganizationId().equals(RIVIGO_ORGANIZATION_ID)) {
      return;
    }
    closeQcTickets(
        ticketList,
        unloadingData.getConsignmentId(),
        ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP);
  }

  private void closeQcTickets(
      List<TicketDTO> ticketList, Long consignmentId, String reasonOfClosure) {

    ticketList.forEach(ticketDTO -> closeTicket(ticketDTO, reasonOfClosure));
    handleQcConsignmentBlocker(
        consignmentId, ConsignmentBlockerRequestType.UNBLOCK, QcType.RE_CHECK);
    handleQcConsignmentBlocker(
        consignmentId, ConsignmentBlockerRequestType.UNBLOCK, QcType.MEASUREMENT);
    zoomBackendAPIClientService.updateQcCheck(consignmentId, false);
  }

  public Boolean check(ConsignmentCompletionEventDTO completionData, Consignment consignment) {
    Map<String, Object> bindings = getVariablesMapToApplyQCRules(completionData, consignment);
    if (bindings.isEmpty()) {
      return false;
    }
    log.info(
        "Calling QCRuleEngine to getRulesFromDBAndApply cnote: {} bindings Map: {}",
        consignment.getCnote(),
        bindings);
    return qcRuleEngine.getRulesFromDBAndApply(bindings, "QC_CHECK");
  }

  public Boolean isMeasurementQcRequired(ConsignmentCompletionEventDTO completionData) {

    if (completionData.getClientClusterMetadataDTO() == null) return Boolean.FALSE;
    if (completionData.getClientClusterMetadataDTO().getMeasurementCheckNeeded() != Boolean.TRUE)
      return Boolean.FALSE;

    if (completionData.getClientClusterMetadataDTO().getQcMeasurementTicketProbability() == null)
      return Boolean.FALSE;

    return Math.random()
        <= completionData.getClientClusterMetadataDTO().getQcMeasurementTicketProbability() / 100.0;
  }

  private void fillClientMetadata(
      ConsignmentCompletionEventDTO completionData, Consignment consignment) {
    if (consignment == null) {
      throw new ZoomException("Consignment is not present");
    }
    if (completionData == null) {
      throw new ZoomException(
          "CompletionData is not present for consignment with cnote: " + consignment.getCnote());
    }

    ClientEntityMetadata clusterMetadata =
        clientEntityMetadataService.getClientClusterMetadata(consignment);

    PinCode pincode = pincodeService.findByCode(consignment.getFromPinCode());
    ClientEntityMetadata pincodeMetadata =
        clientEntityMetadataService.getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            ClientEntityType.PINCODE,
            pincode.getId(),
            consignment.getClient().getId(),
            ClientEntityMetadata.getDefaultLongValue(),
            OperationalStatus.ACTIVE);

    if (clusterMetadata != null) {
      completionData.setClientClusterMetadataDTO(
          objectMapper.convertValue(clusterMetadata.getMetadata(), ClientClusterMetadataDTO.class));
    }
    if (pincodeMetadata != null) {
      completionData.setClientPincodeMetadataDTO(
          objectMapper.convertValue(pincodeMetadata.getMetadata(), ClientPincodeMetadataDTO.class));
    }
  }

  public void consumeCompletionEvent(ConsignmentCompletionEventDTO completionData) {
    Consignment consignment =
        consignmentService.getConsignmentById(completionData.getConsignmentId());
    if (consignment == null) {
      throw new ZoomException(
          "No consignment exists with id :" + completionData.getConsignmentId());
    }
    try {
      zoomBackendAPIClientService.triggerPolicyGeneration(completionData.getConsignmentId());
    } catch (Exception e) {
      log.info(
          "Policy trigger failed for consignment with id {} ",
          completionData.getConsignmentId(),
          e);
    }
    sendCodDodSms(completionData, consignment);
    log.info(
        "cnote: {} isPrimaryConsignment: {} cnoteType: {}",
        consignment.getCnote(),
        consignmentService.isPrimaryConsignment(consignment.getCnote()),
        consignment.getCnoteType());
    if (!consignmentService.isPrimaryConsignment(consignment.getCnote())
        || !CnoteType.NORMAL.equals(consignment.getCnoteType())) {
      return;
    }
    fillClientMetadata(completionData, consignment);
    boolean reCheckQcNeeded = check(completionData, consignment);
    boolean measurementQcNeeded = isMeasurementQcRequired(completionData);
    log.info(
        "cnote: {} reCheckQcNeeded: {} measurementQcNeeded: {}",
        consignment.getCnote(),
        reCheckQcNeeded,
        measurementQcNeeded);
    if (!measurementQcNeeded && !reCheckQcNeeded) {
      return;
    }
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService
            .getTicketsByCnoteAndType(completionData.getCnote(), getQcTicketTypes())
            .stream()
            .filter(ticketDTO -> isOpenQcTicket().test(ticketDTO))
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(ticketList)) {
      return;
    }
    log.info(
        "cnote: {} reCheckQcNeeded: {} measurementQcNeeded {} locationId {}",
        consignment.getCnote(),
        reCheckQcNeeded,
        measurementQcNeeded,
        consignment.getLocationId());
    Long groupId = null;
    Boolean autoClose = false;
    if (ConsignmentStatus.RECEIVED_AT_OU.equals(consignment.getStatus())) {
      GroupDTO group =
          zoomTicketingAPIClientService.getGroupId(
              consignment.getLocationId(), ZoomTicketingConstant.QC_GROUP_NAME, LocationType.OU);
      Location location = locationService.getLocationById(consignment.getLocationId());
      groupId = group == null ? null : group.getId();
      autoClose = (group == null) && location.getOrganizationId().equals(RIVIGO_ORGANIZATION_ID);
    }
    log.info(
        "cnote: {}  locationId: {}  groupId: {}",
        consignment.getCnote(),
        consignment.getLocationId(),
        groupId);
    createTicketsIfNeeded(reCheckQcNeeded, measurementQcNeeded, groupId, consignment, autoClose);
    if (autoClose) {
      return;
    }
    if (reCheckQcNeeded && RIVIGO_ORGANIZATION_ID == consignment.getOrganizationId()) {
      handleQcConsignmentBlocker(
          consignment.getId(), ConsignmentBlockerRequestType.BLOCK, QcType.RE_CHECK);
    }
    zoomBackendAPIClientService.updateQcCheck(consignment.getId(), true);
  }

  private void handleQcConsignmentBlocker(
      Long consignmentId, ConsignmentBlockerRequestType requestType, QcType qcType) {
    ConsignmentBlockerRequestDTO qcBlocker =
        ConsignmentBlockerRequestDTO.builder()
            .consignmentId(consignmentId)
            .isActive(Boolean.TRUE)
            .requestType(requestType)
            .reason(ReasonConstant.QC_VALIDATION_BLOCKER_REASON)
            .subReason(ReasonConstant.QC_VALIDATION_BLOCKER_SUB_REASON)
            .build();
    if (qcType == QcType.RE_CHECK) {
      qcBlocker.setReason(ReasonConstant.QC_VALIDATION_BLOCKER_REASON);
      qcBlocker.setSubReason(ReasonConstant.QC_VALIDATION_BLOCKER_SUB_REASON);
    } else {
      qcBlocker.setReason(ReasonConstant.QC_MEASUREMENT_BLOCKER_REASON);
      qcBlocker.setSubReason(ReasonConstant.QC_MEASUREMENT_BLOCKER_SUB_REASON);
    }
    zoomBackendAPIClientService.handleConsignmentBlocker(qcBlocker);
  }

  private void createTicketsIfNeeded(
      Boolean reCheckQcNeeded,
      Boolean measurementQcNeeded,
      Long groupId,
      Consignment consignment,
      Boolean autoClose) {
    if (reCheckQcNeeded) {
      TicketDTO dto = getBasicQcTicketDTO(groupId, consignment.getCnote());
      dto.setTypeId(ZoomTicketingConstant.QC_RECHECK_TYPE_ID);
      dto.setSubject(ZoomTicketingConstant.QC_RECHECK_TASK_CREATION_MESSAGE);
      log.info("recheck qc task being created");
      dto = zoomTicketingAPIClientService.createTicket(dto);
      if (autoClose) {
        ticketingService.closeTicket(
            dto, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP);
      }
    }
    if (measurementQcNeeded) {
      TicketDTO dto = getBasicQcTicketDTO(groupId, consignment.getCnote());
      dto.setTypeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID);
      dto.setSubject(ZoomTicketingConstant.QC_MEASUREMENT_TASK_CREATION_MESSAGE);
      dto = zoomTicketingAPIClientService.createTicket(dto);
      if (autoClose) {
        ticketingService.closeTicket(
            dto, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP);
      }
    }
  }

  private TicketDTO getBasicQcTicketDTO(Long groupId, String cnote) {
    return TicketDTO.builder()
        .assigneeId(groupId)
        .entityId(cnote)
        .assigneeType(groupId == null ? AssigneeType.NONE : AssigneeType.GROUP)
        .source(TicketSource.INTERNAL)
        .title(ZoomTicketingConstant.QC_TASK_TITLE)
        .build();
  }

  private void sendCodDodSms(ConsignmentCompletionEventDTO eventDTO, Consignment consignment) {
    ConsignmentCodDod codDod = consignmentCodDodService.getActiveCodDod(consignment.getId());
    if (codDod == null) {
      log.debug("CodDod is not activated for consignment with cnote: {}", consignment.getCnote());
      return;
    }
    DateTimeFormatter formatter =
        DateTimeFormat.forPattern("dd-MM-yyyy").withZone(DateTimeZone.forID("Asia/Kolkata"));
    String dateStr = formatter.print(consignment.getPromisedDeliveryDateTime());

    StringBuilder sb = new StringBuilder();
    sb.append("Dispatched: Your consignment #")
        .append(eventDTO.getCnote())
        .append(" from ")
        .append(consignment.getConsignorName())
        .append(" will be delivered on or before ")
        .append(dateStr)
        .append(". Please keep ")
        .append(codDod.getPaymentType().displayName())
        .append(" for Rs ")
        .append(codDod.getAmount())
        .append(" in favour of ")
        .append(codDod.getInFavourOf())
        .append(" ready for pick up. ");
    String smsString = sb.toString();
    smsService.sendSms(consignment.getConsigneePhone(), smsString);
  }

  public List<String> getQcTicketTypes() {
    return Arrays.asList(
        ZoomTicketingConstant.QC_RECHECK_TYPE_ID.toString(),
        ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString());
  }

  private Map<String, Object> getVariablesMapToApplyQCRules(
      ConsignmentCompletionEventDTO completionData, Consignment consignment) {
    Map<String, Object> bindings = new HashMap<>();
    populateQcConstants(bindings);
    bindings.put(RuleEngineVariableNameConstant.CLIENT_TYPE, consignment.getCnoteType().name());
    if (completionData.getClientPincodeMetadataDTO() != null
        && completionData.getClientPincodeMetadataDTO().getCount() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.NUMBER_OF_CN,
          completionData.getClientPincodeMetadataDTO().getCount().doubleValue());
    } else {
      log.info("one of the NUMBER_OF_CN param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }
    if (consignment.getTotalBoxes() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.TOTAL_BOXES, (double) consignment.getTotalBoxes());
    } else {
      log.info("Total boxes param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }
    if (consignment.getTotalBoxes() != null && consignment.getWeight() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.WEIGHT_TOTAL_BOXES_DIFF,
          Math.abs(consignment.getWeight() - consignment.getTotalBoxes()));
    } else {
      log.info("Total boxes param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }
    if (consignment.getVolume() != null) {
      bindings.put(RuleEngineVariableNameConstant.VOLUME, consignment.getVolume());
    } else {
      log.info("Volume param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }
    if (consignment.getVolume() != null && consignment.getWeight() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.VOLUME_TO_WEIGHT_RATIO,
          (consignment.getVolume() / consignment.getWeight()));
    } else {
      log.info("Volume or weight param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }
    if (consignment.getWeight() != null
        && completionData.getClientPincodeMetadataDTO() != null
        && completionData.getClientPincodeMetadataDTO().getMinWeight() != null
        && completionData.getClientPincodeMetadataDTO().getMaxWeight() != null) {
      bindings.put(RuleEngineVariableNameConstant.ACTUAL_WEIGHT, consignment.getWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MIN_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMinWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MAX_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMaxWeight());
    } else {
      log.info("one of the ACTUAL_WEIGHT param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }

    Double chargedWeight =
        zoomBillingAPIClientService.getChargedWeightForConsignment(consignment.getCnote());

    if (chargedWeight != null
        && consignment.getWeight() != null
        && consignment.getWeight() > 0.001
        && completionData.getClientPincodeMetadataDTO() != null
        && completionData.getClientPincodeMetadataDTO().getMinChargedWeightPerWeight() != null
        && completionData.getClientPincodeMetadataDTO().getMaxChargedWeightPerWeight() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.CHARGED_WEIGHT_PER_WEIGHT,
          chargedWeight / consignment.getWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MIN_CHARGED_WEIGHT_PER_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMinChargedWeightPerWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MAX_CHARGED_WEIGHT_PER_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMaxChargedWeightPerWeight());
    } else {
      log.info("one of the CHARGED_WEIGHT param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }

    if (consignment.getValue() != null
        && consignment.getWeight() != null
        && consignment.getWeight() > 0.001
        && completionData.getClientPincodeMetadataDTO() != null
        && completionData.getClientPincodeMetadataDTO().getMinInvoicePerWeight() != null
        && completionData.getClientPincodeMetadataDTO().getMaxInvoicePerWeight() != null) {
      bindings.put(
          RuleEngineVariableNameConstant.INVOICE_VALUE_PER_WEIGHT,
          consignment.getValue() / consignment.getWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MIN_INVOICE_VALUE_PER_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMinInvoicePerWeight());
      bindings.put(
          RuleEngineVariableNameConstant.MAX_INVOICE_VALUE_PER_WEIGHT,
          completionData.getClientPincodeMetadataDTO().getMaxInvoicePerWeight());
    } else {
      log.info("one of the INVOICE_VALUE param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }

    if (consignment.getVolumeDetails() != null) {
      List<Double> length =
          consignment
              .getVolumeDetails()
              .stream()
              .map(volumeDetails -> volumeDetails.getLength())
              .collect(Collectors.toList());
      bindings.put("LENGTH", length);
      List<Double> breadth =
          consignment
              .getVolumeDetails()
              .stream()
              .map(volumeDetails -> volumeDetails.getBreadth())
              .collect(Collectors.toList());
      bindings.put("BREADTH", breadth);
      List<Double> height =
          consignment
              .getVolumeDetails()
              .stream()
              .map(volumeDetails -> volumeDetails.getHeight())
              .collect(Collectors.toList());
      bindings.put("HEIGHT", height);
    }
    else {
      log.info("Volume details param is null...returning bindings as emptyMap");
      return Collections.emptyMap();
    }

    return bindings;
  }

  private  void populateQcConstants(Map<String, Object> bindings)
  {
    Double minimumNumberOfCnRequired =
        zoomPropertyService.getDouble(ZoomPropertyName.MINIMUM_NUMBER_OF_CN_REQUIRED, 30.0);

    String requiredClientType =
        zoomPropertyService.getString(
            ZoomPropertyName.REQUIRED_CLIENT_TYPE, CnoteType.NORMAL.name());

    bindings.put(
        RuleEngineVariableNameConstant.MINIMUM_NUMBER_OF_CN_REQUIRED, minimumNumberOfCnRequired);
    bindings.put(RuleEngineVariableNameConstant.REQUIRED_CLIENT_TYPE, requiredClientType);
    bindings.put(
        ZoomPropertyName.MAXIMUM_VOLUME_ALLOWED_WITHOUT_QC.name(),
        zoomPropertyService.getDouble(ZoomPropertyName.MAXIMUM_VOLUME_ALLOWED_WITHOUT_QC, 35.0));
    bindings.put(
        ZoomPropertyName.WEIGHT_TOTAL_BOXES_DIFF_QC.name(),
        zoomPropertyService.getDouble(ZoomPropertyName.WEIGHT_TOTAL_BOXES_DIFF_QC, 0.001));
    bindings.put(
        ZoomPropertyName.QC_DIMENSION_VALUE.name(),
        zoomPropertyService.getDouble(ZoomPropertyName.QC_DIMENSION_VALUE, 1.0));
    bindings.put(
        ZoomPropertyName.MAXIMUM_VOLUME_WEIGHT_RATIO_ALLOWED_WITHOUT_QC.name(),
        zoomPropertyService.getDouble(
            ZoomPropertyName.MAXIMUM_VOLUME_WEIGHT_RATIO_ALLOWED_WITHOUT_QC, 4.0));
    bindings.put(
        ZoomPropertyName.MAXIMUM_VOLUME_WITH_RATIO_CONSTRAINT_QC.name(),
        zoomPropertyService.getDouble(
            ZoomPropertyName.MAXIMUM_VOLUME_WITH_RATIO_CONSTRAINT_QC, 15.0));
    bindings.put(
        ZoomPropertyName.MAXIMUM_RATIO_WITH_VOLUME_CONSTRAINT_QC.name(),
        zoomPropertyService.getDouble(
            ZoomPropertyName.MAXIMUM_RATIO_WITH_VOLUME_CONSTRAINT_QC, 2.0));
    bindings.put(
        ZoomPropertyName.MAXIMUM_BOXES_WITH_WEIGHT_EQUALS_BOXES_QC.name(),
        zoomPropertyService.getDouble(
            ZoomPropertyName.MAXIMUM_BOXES_WITH_WEIGHT_EQUALS_BOXES_QC, 5.0));
  }

  @Override
  public void consumeCnoteTypeChangeEvent(ConsignmentBasicDTO consignment) {
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService
            .getTicketsByCnoteAndType(consignment.getCnote(), getQcTicketTypes())
            .stream()
            .filter(ticketDTO -> isOpenQcTicket().test(ticketDTO))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(ticketList)) {
      return;
    }
    ticketList.forEach(
        ticketDTO ->
            ticketingService.closeTicket(
                ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_CNOTE_TYPE_CHANGE));
    handleQcConsignmentBlocker(
        consignment.getConsignmentId(), ConsignmentBlockerRequestType.UNBLOCK, QcType.RE_CHECK);
    handleQcConsignmentBlocker(
        consignment.getConsignmentId(), ConsignmentBlockerRequestType.UNBLOCK, QcType.MEASUREMENT);
    zoomBackendAPIClientService.updateQcCheck(consignment.getConsignmentId(), false);
  }

  @Override
  public void consumeCnoteChangeEvent(String oldCnote, String cnote) {
    List<TicketDTO> tickets =
        zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            oldCnote,
            Arrays.asList(
                ZoomTicketingConstant.QC_RECHECK_TYPE_ID.toString(),
                ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString(),
                ZoomTicketingConstant.QC_BLOCKER_TYPE_ID.toString()));
    tickets.forEach(
        ticketDTO -> {
          ticketDTO.setEntityId(cnote);
          zoomTicketingAPIClientService.editTicket(ticketDTO);
        });
  }

  @Override
  public void consumeDepsCreationEvent(String cnote, Long consignmentId) {
    Consignment consignment;
    if (cnote != null) {
      String primaryCnote = cnote.split("-")[0];
      consignment = consignmentService.getConsignmentByCnote(primaryCnote);
      if (consignment == null) {
        throw new ZoomException("No consignment exists with cnote %s ", primaryCnote);
      }
    } else {
      consignment = consignmentService.getConsignmentById(consignmentId);
      if (consignment == null) {
        throw new ZoomException("No consignment exists with id %s ", consignmentId);
      }
    }

    ConsignmentBlockerRequestDTO qcBlocker =
        ConsignmentBlockerRequestDTO.builder()
            .consignmentId(consignment.getId())
            .isActive(Boolean.TRUE)
            .requestType(ConsignmentBlockerRequestType.UNBLOCK)
            .reason(QC_BLOCKER_REASON)
            .subReason(QC_BLOCKER_SUB_REASON)
            .build();
    zoomBackendAPIClientService.handleConsignmentBlocker(qcBlocker);
    handleQcConsignmentBlocker(
        consignment.getId(), ConsignmentBlockerRequestType.UNBLOCK, QcType.RE_CHECK);
    handleQcConsignmentBlocker(
        consignment.getId(), ConsignmentBlockerRequestType.UNBLOCK, QcType.MEASUREMENT);
    zoomBackendAPIClientService.updateQcCheck(consignment.getId(), false);
    List<TicketDTO> tickets =
        zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            consignment.getCnote(), getQcTicketTypes());
    tickets.forEach(
        ticketDTO -> {
          if (!TicketStatus.CLOSED.equals(ticketDTO.getStatus())) {
            closeTicket(ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_DEPS_CREATION);
          }
        });
  }

  @Override
  public void consumeQcBlockerTicketClosedEvent(Long ticketId, Long ticketingUserId) {
    if (ticketId == null) {
      return;
    }
    TicketDTO ticketDTO = zoomTicketingAPIClientService.getTicketByTicketId(ticketId);
    if (ticketDTO == null) {
      throw new ZoomException("Error occured while fetching ticket {}", ticketId);
    }
    if (ticketDTO.getTypeId() != ZoomTicketingConstant.QC_BLOCKER_TYPE_ID) {
      return;
    }
    if (ticketDTO.getStatus() != TicketStatus.CLOSED) {
      closeTicket(ticketDTO, ZoomTicketingConstant.QC_BLOCKER_CLOSURE_MESSAGE);
    }
    zoomBackendAPIClientService.handleQcBlockerClosure(ticketId);
  }

  @Override
  public void consumeQcBlockerTicketCreationEvent(Long ticketId, String cnote, Long typeId) {
    if (typeId == null || !typeId.equals(ZoomTicketingConstant.QC_BLOCKER_TYPE_ID)) {
      log.info("Event is ignored as it's typeId is {} ", typeId);
      return;
    }
    Consignment consignment = consignmentService.getConsignmentByCnote(cnote);
    if (CollectionUtils.isEmpty(consignment.getClient().getClientNotificationList())) {
      TicketDTO qcBlockerTicket = zoomTicketingAPIClientService.getTicketByTicketId(ticketId);
      closeTicket(qcBlockerTicket, ZoomTicketingConstant.QC_BLOCKER_AUTO_CLOSURE_MESSAGE);
      zoomBackendAPIClientService.handleConsignmentBlocker(
          ConsignmentBlockerRequestDTO.builder()
              .consignmentId(consignment.getId())
              .requestType(ConsignmentBlockerRequestType.UNBLOCK)
              .isActive(Boolean.TRUE)
              .reason(ReasonConstant.QC_BLOCKER_REASON)
              .subReason(ReasonConstant.QC_BLOCKER_SUB_REASON)
              .build());
      return;
    }
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            cnote,
            Collections.singletonList(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString()));
    if (CollectionUtils.isEmpty(ticketList)) {
      log.info("No qc measurement tickets found for cnote {} ", cnote);
      return;
    }
    TicketDTO recentQcMeasurementTicket = ticketList.get(ticketList.size() - 1);
    Optional<TicketCommentDTO> urlOptional =
        zoomTicketingAPIClientService
            .getComments(recentQcMeasurementTicket.getId())
            .stream()
            .filter(comment -> comment.getAttachmentURL() != null)
            .reduce((first, second) -> second);
    String imageZipUrl = "";
    if (urlOptional.isPresent()) {
      imageZipUrl = urlOptional.get().getAttachmentURL();
    }
    QcBlockerActionParams qcBlockerActionParams =
        QcBlockerActionParams.builder()
            .consignmentId(consignment.getId())
            .ticketId(ticketId)
            .build();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    Integer expiryHours =
        zoomPropertyService.getInteger(ZoomPropertyName.QC_BLOCKER_TICKET_EXPIRY_HOURS, 72);
    qcBlockerActionParamsRedisRepository.set(
        uuid, qcBlockerActionParams, expiryHours, TimeUnit.HOURS);

    String subject = zoomPropertyService.getString(ZoomPropertyName.QC_BLOCKER_EMAIL_SUBJECT);
    String bodyTemplate = zoomPropertyService.getString(ZoomPropertyName.QC_BLOCKER_EMAIL_BODY);

    Location fromLocation = locationService.getLocationById(consignment.getFromId());
    Location toLocation = locationService.getLocationById(consignment.getToId());
    Location currentLocation = locationService.getLocationById(consignment.getLocationId());

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("consignmentId", consignment.getId().toString());
    valuesMap.put("fromLocationCode", fromLocation.getCode());
    valuesMap.put("fromLocationName", fromLocation.getName());
    valuesMap.put("toLocationCode", toLocation.getCode());
    valuesMap.put("toLocationName", toLocation.getName());
    valuesMap.put("currentLocationCode", currentLocation.getCode());
    valuesMap.put("currentLocationName", currentLocation.getName());
    valuesMap.put("cnote", consignment.getCnote());
    valuesMap.put("consigneeAddress", consignment.getConsigneeAddress());
    valuesMap.put("consignorAddress", consignment.getConsignorAddress());
    valuesMap.put("imageZipUrl", imageZipUrl);
    valuesMap.put("uuid", uuid);
    StrSubstitutor sub = new StrSubstitutor(valuesMap);

    emailService.sendEmail(
        EmailConstant.SERVICE_EMAIL_ID,
        getToRecepients(consignment),
        getCcRecepients(consignment),
        Collections.emptyList(),
        sub.replace(subject),
        sub.replace(bodyTemplate),
        null);
  }

  public Collection<String> getToRecepients(Consignment consignment) {
    if (consignment.getOrganizationId() == RIVIGO_ORGANIZATION_ID) {
      return consignment.getClient().getClientNotificationList();
    }
    Organization organization = organizationService.getById(consignment.getOrganizationId());
    return Collections.singleton(organization.getEmail());
  }

  public Collection<String> getCcRecepients(Consignment consignment) {
    User sam = userMasterService.getById(consignment.getClient().getSamUserId());
    if (sam == null) {
      throw new ZoomException(
          "Sam user is missing for client {} ", consignment.getClient().getId());
    }
    return Collections.singleton(sam.getEmail());
  }

  private void placeValidationBlockerForBFCNsAtFirstRivigoLocation(
      Long consignmentId, Long locationId) {
    Long orgId = consignmentService.getOrganizationIdFromCnId(consignmentId);
    if (orgId == null) {
      return;
    }
    if (orgId.equals(RIVIGO_ORGANIZATION_ID)) {
      return;
    }
    List<LocationTag> nonRivigoLocationTag = Arrays.asList(LocationTag.BF, LocationTag.DF);
    Long firstLocationId =
        consignmentScheduleService
            .getActivePlan(consignmentId)
            .stream()
            .filter(
                cs ->
                    cs.getLocationType() == LocationTypeV2.LOCATION
                        && !nonRivigoLocationTag.contains(cs.getLocationTag()))
            .min(Comparator.comparing(ConsignmentSchedule::getSequence))
            .map(ConsignmentSchedule::getLocationId)
            .orElse(null);
    if (locationId.equals(firstLocationId)) {
      handleQcConsignmentBlocker(
          consignmentId, ConsignmentBlockerRequestType.BLOCK, QcType.RE_CHECK);
    }
  }
}
