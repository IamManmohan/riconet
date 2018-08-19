package com.rivigo.riconet.core.service.impl;

import static com.rivigo.riconet.core.constants.ReasonConstant.QC_BLOCKER_REASON;
import static com.rivigo.riconet.core.constants.ReasonConstant.QC_BLOCKER_SUB_REASON;
import static com.rivigo.riconet.core.predicates.TicketPredicate.isOpenQcTicket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.constants.ConsignmentConstant;
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
import com.rivigo.riconet.core.service.ConsignmentCodDodService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.QcService;
import com.rivigo.riconet.core.service.SmsService;
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
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.ClientEntityMetadata;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentCodDod;
import com.rivigo.zoom.common.model.PinCode;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.PinCodeRepository;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ConsignmentCodDodService consignmentCodDodService;

  @Autowired private ClientEntityMetadataServiceImpl clientEntityMetadataService;

  @Autowired private PinCodeRepository pinCodeRepository;

  @Autowired private AdministrativeEntityRepository administrativeEntityRepository;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private LocationService locationService;

  @Autowired private ZoomBillingAPIClientService zoomBillingAPIClientService;

  @Autowired private EmailService emailService;

  @Autowired private UserMasterService userMasterService;

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
    ticketList.forEach(
        ticketDTO -> {
          ticketDTO.setAssigneeId(null);
          ticketDTO.setAssigneeType(AssigneeType.NONE);
          zoomTicketingAPIClientService.editTicket(ticketDTO);
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
    GroupDTO group =
        zoomTicketingAPIClientService.getGroupId(
            unloadingData.getLocationId(), ZoomTicketingConstant.QC_GROUP_NAME, LocationType.OU);
    ticketList.forEach(
        ticketDTO -> {
          ticketDTO.setAssigneeId(group == null ? null : group.getId());
          ticketDTO.setAssigneeType(group == null ? AssigneeType.NONE : AssigneeType.GROUP);
          zoomTicketingAPIClientService.editTicket(ticketDTO);
        });
    if (group != null) {
      return;
    }
    Location location = locationService.getLocationById(unloadingData.getLocationId());
    if (!location.getOrganizationId().equals(ConsignmentConstant.RIVIGO_ORGANIZATION_ID)) {
      return;
    }
    ticketList.forEach(
        ticketDTO ->
            closeTicket(ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP));
    handleQcConsignmentBlocker(
        unloadingData.getConsignmentId(), ConsignmentBlockerRequestType.UNBLOCK);
    zoomBackendAPIClientService.updateQcCheck(unloadingData.getConsignmentId(), false);
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
    if (!completionData.getClientClusterMetadataDTO().getMeasurementCheckNeeded())
      return Boolean.FALSE;

    if (completionData.getClientClusterMetadataDTO().getQcMeasurementTicketProbability() == null)
      return Boolean.FALSE;

    return Math.random()
        <= completionData.getClientClusterMetadataDTO().getQcMeasurementTicketProbability()/100.0;
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

    PinCode pincode = pinCodeRepository.findByCode(consignment.getFromPinCode());
    ClientEntityMetadata pincodeMetadata =
        clientEntityMetadataService.getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            ClientEntityType.PINCODE,
            pincode.getId(),
            consignment.getClient().getId(),
            null,
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
      autoClose =
          (group == null)
              && location.getOrganizationId().equals(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
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
    if (reCheckQcNeeded) {
      handleQcConsignmentBlocker(consignment.getId(), ConsignmentBlockerRequestType.BLOCK);
    }
    zoomBackendAPIClientService.updateQcCheck(consignment.getId(), true);
  }

  private void handleQcConsignmentBlocker(
      Long consignmentId, ConsignmentBlockerRequestType requestType) {
    ConsignmentBlockerRequestDTO qcValidationBlocker =
        ConsignmentBlockerRequestDTO.builder()
            .consignmentId(consignmentId)
            .isActive(Boolean.TRUE)
            .requestType(requestType)
            .reason(ReasonConstant.QC_VALIDATION_BLOCKER_REASON)
            .subReason(ReasonConstant.QC_VALIDATION_BLOCKER_SUB_REASON)
            .build();
    zoomBackendAPIClientService.handleConsignmentBlocker(qcValidationBlocker);
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
        closeTicket(dto, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP);
      }
    }
    if (measurementQcNeeded) {
      TicketDTO dto = getBasicQcTicketDTO(groupId, consignment.getCnote());
      dto.setTypeId(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID);
      dto.setSubject(ZoomTicketingConstant.QC_MEASUREMENT_TASK_CREATION_MESSAGE);
      dto = zoomTicketingAPIClientService.createTicket(dto);
      if (autoClose) {
        closeTicket(dto, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_NO_QC_GROUP);
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

    Double minimumNumberOfCnRequired =
        zoomPropertyService.getDouble(ZoomPropertyName.MINIMUM_NUMBER_OF_CN_REQUIRED, 30.0);

    String requiredClientType =
        zoomPropertyService.getString(
            ZoomPropertyName.REQUIRED_CLIENT_TYPE, CnoteType.NORMAL.name());
    bindings.put(
        RuleEngineVariableNameConstant.MINIMUM_NUMBER_OF_CN_REQUIRED, minimumNumberOfCnRequired);
    bindings.put(RuleEngineVariableNameConstant.REQUIRED_CLIENT_TYPE, requiredClientType);
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

    return bindings;
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
            closeTicket(
                ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_MESSAGE_CNOTE_TYPE_CHANGE));
    handleQcConsignmentBlocker(
        consignment.getConsignmentId(), ConsignmentBlockerRequestType.UNBLOCK);
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
  public void consumeDepsCreationEvent(String cnote) {
    String primaryCnote = cnote.split("-")[0];
    Consignment consignment = consignmentService.getConsignmentByCnote(primaryCnote);

    if (consignment == null) {
      throw new ZoomException("No consignment exists with cnote %s ", primaryCnote);
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
    handleQcConsignmentBlocker(consignment.getId(), ConsignmentBlockerRequestType.UNBLOCK);
    zoomBackendAPIClientService.updateQcCheck(consignment.getId(), false);
    List<TicketDTO> tickets =
        zoomTicketingAPIClientService.getTicketsByCnoteAndType(primaryCnote, getQcTicketTypes());
    tickets.forEach(
        ticketDTO -> {
          if (!TicketStatus.CLOSED.equals(ticketDTO.getStatus())) {
            closeTicket(ticketDTO, ZoomTicketingConstant.QC_AUTO_CLOSURE_DEPS_CREATION);
          }
        });
  }

  @Override
  public void consumeQcBlockerTicketClosedEvent(Long ticketId) {
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
      closeTicket(ticketDTO, ZoomTicketingConstant.QC_BLOCKER_CLOSURE_MESSAE);
    }
    zoomBackendAPIClientService.handleQcBlockerClosure(ticketId);
  }

  @Override
  public void consumeQcBlockerTicketCreationEvent(String cnote) {
    List<TicketDTO> ticketList =
        zoomTicketingAPIClientService.getTicketsByCnoteAndType(
            cnote,
            Collections.singletonList(ZoomTicketingConstant.QC_MEASUREMENT_TYPE_ID.toString()));
    if (CollectionUtils.isEmpty(ticketList)) {
      log.info("No qc measurement tickets found for cnote {} ", cnote);
      return;
    }
    TicketDTO recentTicket = ticketList.get(ticketList.size() - 1);
    List<String> urlList =
        zoomTicketingAPIClientService
            .getComments(recentTicket.getId())
            .stream()
            .filter(comment -> comment.getAttachmentURL() != null)
            .map(TicketCommentDTO::getAttachmentURL)
            .collect(Collectors.toList());
    StringBuilder imageLinks = new StringBuilder();
    imageLinks.append("<br>");
    for (String url : urlList) {
      imageLinks.append("<a href='").append(url).append("'>abcd</a><br>");
    }

    Consignment consignment = consignmentService.getConsignmentByCnote(cnote);
    String subject = zoomPropertyService.getString(ZoomPropertyName.QC_BLOCKER_EMAIL_SUBJECT);
    String bodyTemplate = zoomPropertyService.getString(ZoomPropertyName.QC_BLOCKER_EMAIL_BODY);

    Location fromLocation = locationService.getLocationById(consignment.getFromId());
    Location toLocation = locationService.getLocationById(consignment.getFromId());
    Location currentLocation = locationService.getLocationById(consignment.getLocationId());

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("consignmentId", consignment.getId().toString());
    valuesMap.put("fromLocationCode", fromLocation.getCode());
    valuesMap.put("fromLocationName", fromLocation.getName());
    valuesMap.put("toLocationCode", toLocation.getCode());
    valuesMap.put("toLocationName", toLocation.getName());
    valuesMap.put("currentLocationCode", currentLocation.getCode());
    valuesMap.put("currentLocationName", currentLocation.getName());
    valuesMap.put("urlList", imageLinks.toString());
    StrSubstitutor sub = new StrSubstitutor(valuesMap);

    emailService.sendEmail(
        EmailConstant.SERVICE_EMAIL_ID,
        getToRecepients(consignment),
        getCcRecepients(consignment),
        Collections.emptyList(),
        subject,
        sub.replace(bodyTemplate),
        null);
  }

  public Collection<String> getToRecepients(Consignment consignment) {
    // TODO: tata update toRecepients
    return Arrays.asList("update@rivigo.com");
  }

  public Collection<String> getCcRecepients(Consignment consignment) {
    User sam = userMasterService.getById(consignment.getClient().getSamUserId());
    if (sam == null) {
      throw new ZoomException(
          "Sam user is missing for client {} ", consignment.getClient().getId());
    }
    return Collections.singleton(sam.getEmail());
  }
}
