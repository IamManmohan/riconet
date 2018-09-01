package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
@Slf4j
@Service
public class TicketingServiceImpl implements TicketingService {

  private final EmailSenderService emailSenderService;

  @Autowired private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Autowired
  public TicketingServiceImpl(EmailSenderService emailSenderService) {
    this.emailSenderService = emailSenderService;
  }

  @Override
  public void sendTicketingEventsEmail(NotificationDTO notificationDTO) {
    EventName eventName = notificationDTO.getEventName();
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
    EventName eventName = notificationDTO.getEventName();
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
    log.info("PriorityTicketTypes: {}", zoomPropertyService.getString(ZoomPropertyName.PRIORITY_TICKET_TYPE));
    log.info("TicketTypeID : {}",metadata.get(TicketingFieldName.TYPE_ID.name()));
    log.info("TicketType : {}",metadata.get(TicketingFieldName.TICKET_TYPE));
    List<Long> ticketTypes =
        Stream.of(zoomPropertyService.getString(ZoomPropertyName.PRIORITY_TICKET_TYPE).split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
    List<Long> closableTicketTypes =
        Stream.of(zoomPropertyService.getString(ZoomPropertyName.AUTOCLOSABLE_PRIORITY_TICKET_TYPE).split(",")).map(Long::parseLong)
            .collect(Collectors.toList());
    if (ticketTypes== null || ticketTypes.isEmpty()) {
      log.info("No ticket type found for which CN's are to be set as priority");
      return;
    }

    if (ticketTypes.contains(
        Long.parseLong(metadata.get(TicketingFieldName.TYPE_ID.name())))) {
      zoomBackendAPIClientService.setPriorityMapping(
          metadata.get(TicketingFieldName.ENTITY_ID.name()), PriorityReasonType.TICKET);
      log.info("AutoClosablePriorityTicketTypes: {}", zoomPropertyService.getString(ZoomPropertyName.AUTOCLOSABLE_PRIORITY_TICKET_TYPE));
      if (closableTicketTypes.contains(
          Long.parseLong(metadata.get(TicketingFieldName.TYPE_ID.name())))) {
        TicketDTO dto = new TicketDTO();
        dto.setId(Long.parseLong(metadata.get(TicketingFieldName.TICKET_ID.name())));
        dto.setReasonOfClosure("Ticket is auto-closable after creation");
        dto.setStatus(com.rivigo.riconet.core.enums.zoomticketing.TicketStatus.CLOSED);
        zoomTicketingAPIClientService.editTicket(dto);
      }
    }
  }
}
