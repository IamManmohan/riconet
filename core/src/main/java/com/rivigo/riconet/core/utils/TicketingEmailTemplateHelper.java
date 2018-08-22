package com.rivigo.riconet.core.utils;

import com.rivigo.riconet.core.enums.TicketingFieldName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
@Slf4j
public class TicketingEmailTemplateHelper {

  private TicketingEmailTemplateHelper() {}

  public static String getSubject(Map<String, String> metadata) {
    StringBuilder subject = new StringBuilder();

    getValueFromMap(metadata, TicketingFieldName.TICKET_ID)
        .ifPresent(v -> subject.append("Ticket ").append(v));
    getValueFromMap(metadata, TicketingFieldName.TICKET_TYPE)
        .ifPresent(v -> subject.append(" | ").append(v));
    return subject.toString();
  }

  public static Optional<List<String>> getRecipientList(Map<String, String> metadata) {

    List<String> emailList = new ArrayList<>();

    getEmailList(metadata, TicketingFieldName.CREATOR_EMAIL).ifPresent(emailList::addAll);
    getEmailList(metadata, TicketingFieldName.ASSIGNEE_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, TicketingFieldName.CC_USER_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, TicketingFieldName.OWNER_EMAIL_LIST).ifPresent(emailList::addAll);

    if (CollectionUtils.isEmpty(emailList)) {
      return Optional.empty();
    }
    return Optional.of(emailList);
  }

  public static Optional<List<String>> getEmailList(
      Map<String, String> map, TicketingFieldName fieldName) {

    Optional<String> emailListString = getValueFromMap(map, fieldName);

    if (!emailListString.isPresent()) {
      return Optional.empty();
    }

    String[] emailList = emailListString.get().split(",");

    List<String> emails = new ArrayList<>();
    for (String email : emailList) {
      if (StringUtils.isEmpty(email)) {
        continue;
      }
      emails.add(email);
    }
    return Optional.of(emails);
  }

  public static Optional<String> getValueFromMap(
      Map<String, String> map, TicketingFieldName fieldName) {
    if (null == map || !map.containsKey(fieldName.toString())) {
      return Optional.empty();
    }
    return Optional.of(map.get(fieldName.toString()));
  }

  public static String getTicketCreationEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(TicketingFieldName.CREATOR_EMAIL.toString()))
        .append(" has created a new ticket of the issue type ")
        .append(metadata.get(TicketingFieldName.TICKET_TYPE.toString()))
        .append(". The severity of the ticket is ")
        .append(metadata.get(TicketingFieldName.SEVERITY.toString()))
        .append(". The SLA for solving the ticket is ")
        .append(metadata.get(TicketingFieldName.SLA.toString()))
        .append(". The ticket has been assigned to the ")
        .append(metadata.get(TicketingFieldName.OWNER_GROUP_NAME_OR_OWNER_EMAIL.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCommentCreationEmailBody(Map<String, String> metadata) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder = stringBuilder
        .append(metadata.get(TicketingFieldName.CREATOR_EMAIL.toString()))
        .append(" has commented.<br>")
        .append(metadata.get(TicketingFieldName.COMMENT_TEXT.toString()));
    if( metadata.containsKey(TicketingFieldName.S3URL.toString())) {
      stringBuilder = stringBuilder
          .append("<br> <a href=\"")
          .append(metadata.get(TicketingFieldName.S3URL.toString()))
          .append("\">Link to Attachment</a>");
    }
    stringBuilder = stringBuilder.append("<br><br>Regards,<br>Rivigo Tickets<br>");
    return stringBuilder.toString();
  }

  public static String getTicketSeverityChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(TicketingFieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the severity from ")
        .append(metadata.get(TicketingFieldName.OLD_SEVERITY.toString()))
        .append(" to ")
        .append(metadata.get(TicketingFieldName.SEVERITY.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketStatusChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(TicketingFieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the status of the ticket from ")
        .append(metadata.get(TicketingFieldName.OLD_STATUS.toString()))
        .append(" to ")
        .append(metadata.get(TicketingFieldName.STATUS.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketAssigneeChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(TicketingFieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the Assignee from ")
        .append(
            metadata.get(TicketingFieldName.OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append(" to ")
        .append(metadata.get(TicketingFieldName.ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketEscalationChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append("The ticket has been escalated to escalation level : ")
        .append(metadata.get(TicketingFieldName.ESCALATION_LEVEL.toString()))
        .append(" and escalation owner : ")
        .append(metadata.get(TicketingFieldName.ESCALATED_TO_EMAIL.toString()))
        .append(" . The ticket needs to be closed on priority. ")
        .append("<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCcNewPersonAdditionEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(TicketingFieldName.ADDER_EMAIL.toString()))
        .append(" has added ")
        .append(metadata.get(TicketingFieldName.NEWLY_CCED_EMAIL.toString()))
        .append(" in CC to this ticket. ")
        .append("<br><br> Comment history : <br>")
        .append(metadata.get(TicketingFieldName.COMMENT_HISTORY.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }
}
