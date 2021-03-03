package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.ZoomTicketingConstant;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import com.rivigo.riconet.core.service.EmailSenderService;
import com.rivigo.riconet.core.service.TicketingService;
import com.rivigo.riconet.core.service.ZoomTicketingAPIClientService;
import com.rivigo.riconet.core.utils.TicketingEmailTemplateHelper;
import com.rivigo.zoom.util.commons.exception.ZoomException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
@Slf4j
@Service
public class TicketingServiceImpl implements TicketingService {

  private final EmailSenderService emailSenderService;

  private final ZoomTicketingAPIClientService zoomTicketingAPIClientService;

  @Autowired
  public TicketingServiceImpl(
      EmailSenderService emailSenderService,
      ZoomTicketingAPIClientService zoomTicketingAPIClientService) {
    this.emailSenderService = emailSenderService;
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
    if (!this.emailUpdateRequired(eventName, metadata)) {
      log.info(
          "Email update not required for Event: {} EventUID: {} ",
          eventName,
          notificationDTO.getEventUID());
      return;
    }
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
  public void reopenTicketIfClosed(TicketDTO ticketDTO, String reason) {
    zoomTicketingAPIClientService.makeComment(ticketDTO.getId(), reason);
    if (ticketDTO.getStatus() != TicketStatus.CLOSED) {
      log.info("Not reopening ticket {} as status is not closed", ticketDTO);
      return;
    }
    ticketDTO.setStatus(TicketStatus.REOPENED);
    zoomTicketingAPIClientService.editTicket(ticketDTO);
  }

  @Override
  public TicketDTO getRequiredById(Long ticketId) {
    return Optional.ofNullable(zoomTicketingAPIClientService.getById(ticketId))
        .orElseThrow(() -> new ZoomException("Error occured while fetching ticket %s", ticketId));
  }

  @Override
  public void closeTicketIfRequired(TicketDTO ticketDTO, String actionClosureMessage) {
    if (ticketDTO.getStatus() != TicketStatus.CLOSED) {
      log.info("Auto closing ticket");
      closeTicket(ticketDTO, actionClosureMessage);
    }
  }

  private boolean emailUpdateRequired(EventName eventName, Map<String, String> metadata) {
    switch (eventName) {
      case TICKET_CREATION:
      case TICKET_STATUS_CHANGE:
      case TICKET_ESCALATION_CHANGE:
      case TICKET_SEVERITY_CHANGE:
      case TICKET_COMMENT_CREATION:
      case TICKET_CC_NEW_PERSON_ADDITION:
        return false;
      case TICKET_ASSIGNEE_CHANGE:
        return TicketingEmailTemplateHelper.getValueFromMap(
                metadata, TicketingFieldName.GROUP_TYPE_ID)
            .filter(
                s ->
                    ZoomTicketingConstant.groupTypeIdsForAssigneeChangeEmail.contains(
                        Long.valueOf(s)))
            .isPresent();
      default:
        log.info(" Email update not required for event : {}", eventName);
    }
    return false;
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
}
