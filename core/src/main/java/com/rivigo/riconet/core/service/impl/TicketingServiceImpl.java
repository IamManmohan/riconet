package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.utils.TicketingEmailTemplateHelper;
import com.rivigo.zoom.common.enums.PriorityReasonType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
@Slf4j
@Service
public class TicketingServiceImpl implements TicketingService {

  private final EmailSenderService emailSenderService;

  private final ZoomBackendAPIClientService zoomBackendAPIClientService;

  private final ZoomPropertyService zoomPropertyService;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Autowired
  public TicketingServiceImpl(
      EmailSenderService emailSenderService,
      ZoomBackendAPIClientService zoomBackendAPIClientService,
      ZoomPropertyService zoomPropertyService,
      ZoomTicketingAPIClientService zoomTicketingAPIClientService) {
    this.emailSenderService = emailSenderService;
    this.zoomBackendAPIClientService = zoomBackendAPIClientService;
    this.zoomPropertyService = zoomPropertyService;
    this.zoomTicketingAPIClientService = zoomTicketingAPIClientService;
  }

  @Override
  public void sendTicketingEventsEmail(NotificationDTO notificationDTO) {
    EventName eventName = EventName.valueOf(notificationDTO.getEventName());
    log.info("Identified Event : {} ", eventName);
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (null == metadata) {
      log.info(
          "No metadata found for sending email of Event: {} EventUID: {} ",
          eventName,
          notificationDTO.getEventUID());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    Optional<List<String>> toRecipients = this.getRecipients(eventName, metadata);
    String subject = this.getSubject(eventName, metadata);
    String body = this.getBody(eventName, metadata);
    log.info(
        "Sending Ticketing Email. To : {} Subject : {}  Body : {} ", toRecipients, subject, body);
    toRecipients.ifPresent(to -> emailSenderService.sendEmail(to, subject, body));
  }

  @Override
  public void closeTicket(TicketDTO ticketDTO, String reasonOfClosure) {
    if (TicketStatus.NEW.equals(ticketDTO.getStatus())) {
      ticketDTO.setStatus(TicketStatus.IN_PROGRESS);
      zoomTicketingAPIClientService.editTicket(ticketDTO);
    }
    ticketDTO.setReasonOfClosure(reasonOfClosure);
    ticketDTO.setStatus(TicketStatus.CLOSED);
    zoomTicketingAPIClientService.editTicket(ticketDTO);
  }

  @Override
  public TicketDTO getById(Long ticketId) {
    return Optional.ofNullable(zoomTicketingAPIClientService.getTicketByTicketId(ticketId))
        .orElseThrow(() -> new ZoomException("Error occured while fetching ticket {}", ticketId));
  }

  @Override
  public void closeTicketIfRequired(TicketDTO ticketDTO, String actionClosureMessage) {
    if (ticketDTO.getStatus() != TicketStatus.CLOSED) {
      log.info("Auto closing ticket");
      closeTicket(ticketDTO, actionClosureMessage);
    }
  }

  private Optional<List<String>> getRecipients(EventName eventName, Map<String, String> metadata) {
    switch (eventName) {
      case TICKET_CREATION:
      case TICKET_ASSIGNEE_CHANGE:
      case TICKET_STATUS_CHANGE:
      case TICKET_ESCALATION_CHANGE:
      case TICKET_SEVERITY_CHANGE:
        return TicketingEmailTemplateHelper.getRecipientList(metadata);
      case TICKET_COMMENT_CREATION:
        return TicketingEmailTemplateHelper.getCommentEmailRecipientList(metadata);
      case TICKET_CC_NEW_PERSON_ADDITION:
        return TicketingEmailTemplateHelper.getNewlyCcedEmailRecipientList(metadata);
      default:
        log.info(" No Recipient getter function found for event : {}", eventName);
    }
    return Optional.empty();
  }

  private String getSubject(EventName eventName, Map<String, String> metadata) {
    switch (eventName) {
      case TICKET_COMMENT_CREATION:
      case TICKET_CREATION:
      case TICKET_ASSIGNEE_CHANGE:
      case TICKET_STATUS_CHANGE:
      case TICKET_ESCALATION_CHANGE:
      case TICKET_CC_NEW_PERSON_ADDITION:
      case TICKET_SEVERITY_CHANGE:
        return TicketingEmailTemplateHelper.getSubject(metadata);
      default:
        log.info(" No Subject getter function found for event : {}", eventName);
    }
    return "";
  }

  private String getBody(EventName eventName, Map<String, String> metadata) {
    switch (eventName) {
      case TICKET_COMMENT_CREATION:
        return TicketingEmailTemplateHelper.getTicketCommentCreationEmailBody(metadata);
      case TICKET_CREATION:
        return TicketingEmailTemplateHelper.getTicketCreationEmailBody(metadata);
      case TICKET_ASSIGNEE_CHANGE:
        return TicketingEmailTemplateHelper.getTicketAssigneeChangeEmailBody(metadata);
      case TICKET_STATUS_CHANGE:
        return TicketingEmailTemplateHelper.getTicketStatusChangeEmailBody(metadata);
      case TICKET_ESCALATION_CHANGE:
        return TicketingEmailTemplateHelper.getTicketEscalationChangeEmailBody(metadata);
      case TICKET_CC_NEW_PERSON_ADDITION:
        return TicketingEmailTemplateHelper.getTicketCcNewPersonAdditionEmailBody(metadata);
      case TICKET_SEVERITY_CHANGE:
        return TicketingEmailTemplateHelper.getTicketSeverityChangeEmailBody(metadata);
      default:
        log.info(" No body getter function found for event : {}", eventName);
    }
    return "";
  }

  @Override
  public void setPriorityMapping(NotificationDTO notificationDTO) {
    EventName eventName = EventName.valueOf(notificationDTO.getEventName());
    log.info("Identified Event : {} ", eventName);
    Map<String, String> metadata = notificationDTO.getMetadata();
    if (metadata.isEmpty()) {
      log.info(
          "No metadata found for updating priority mapping of Event: {} EventUID: {} ",
          eventName,
          notificationDTO.getEventUID());
      return;
    }
    log.info("Event Metadata : {} ", metadata);
    String ticketType = metadata.get(TicketingFieldName.TICKET_TYPE.name());
    if (ticketType != null
        && TicketEntityType.CN
            .toString()
            .equals(metadata.get(TicketingFieldName.ENTITY_TYPE.name()))) {
      List<String> ticketTypes =
          zoomPropertyService.getStringValues(ZoomPropertyName.PRIORITY_TICKET_TYPE);
      log.info("PriorityTicketTypes: {}", ticketTypes);
      if (CollectionUtils.isEmpty(ticketTypes)) {
        log.info("No ticket type found for which CN's are to be set as priority");
        return;
      }
      if (ticketTypes.contains(ticketType)) {
        String cnote = metadata.get(TicketingFieldName.ENTITY_ID.name());
        if (StringUtils.isEmpty(cnote)) {
          log.error("Invalid entity id for ticket {}", notificationDTO.getEntityId());
          return;
        }
        log.info("setPriorityMapping() called for entity {} :START", cnote);
        zoomBackendAPIClientService.setPriorityMapping(cnote, PriorityReasonType.TICKET);
        log.info("setPriorityMapping() called for entity {} :END,SUCCESS", cnote);
        List<String> closableTicketTypes =
            zoomPropertyService.getStringValues(ZoomPropertyName.AUTOCLOSABLE_PRIORITY_TICKET_TYPE);
        log.info("AutoClosablePriorityTicketTypes: {}", closableTicketTypes);
        if (closableTicketTypes.contains(ticketType)) {
          TicketDTO dto = new TicketDTO();
          dto.setId(notificationDTO.getEntityId());
          dto.setStatus(TicketStatus.NEW);
          closeTicket(dto, ZoomTicketingConstant.PRIORITY_AUTO_CLOSURE_MESSAGE);
        }
      }
    }
  }
}
