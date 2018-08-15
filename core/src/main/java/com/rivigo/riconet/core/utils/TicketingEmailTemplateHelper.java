package com.rivigo.riconet.core.utils;

import com.rivigo.riconet.core.enums.FieldName;
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

    getValueFromMap(metadata, FieldName.TICKET_ID)
        .ifPresent(v -> subject.append("Ticket ").append(v));
    getValueFromMap(metadata, FieldName.TICKET_TYPE)
        .ifPresent(v -> subject.append(" | ").append(v));
    return subject.toString();
  }

  public static Optional<List<String>> getRecipientList(Map<String, String> metadata) {

    List<String> emailList = new ArrayList<>();

    getEmailList(metadata, FieldName.CREATOR_EMAIL).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.ASSIGNEE_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.CC_USER_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.OWNER_EMAIL_LIST).ifPresent(emailList::addAll);

    if (CollectionUtils.isEmpty(emailList)) {
      return Optional.empty();
    }
    return Optional.of(emailList);
  }

  public static Optional<List<String>> getEmailList(Map<String, String> map, FieldName fieldName) {

    Optional<String> emailListString = getValueFromMap(map, fieldName);

    if (!emailListString.isPresent()) {
      return Optional.empty();
    }

    String[] emailList = emailListString.get().split(",");

    List<String> emails = new ArrayList<>();
    for (String email : emailList) {
      if (StringUtils.isEmpty(email)) {
        emails.add(email);
      }
    }
    return Optional.of(emails);
  }

  public static Optional<String> getValueFromMap(Map<String, String> map, FieldName fieldName) {
    if (!map.containsKey(fieldName.toString())) {
      return Optional.empty();
    }
    return Optional.of(map.get(fieldName.toString()));
  }

  public static String getTicketCreationEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.CREATOR_EMAIL.toString()))
        .append("has created a new ticket of the issue type ")
        .append(metadata.get(FieldName.TICKET_TYPE.toString()))
        .append(". The severity of the ticket is ")
        .append(metadata.get(FieldName.SEVERITY.toString()))
        .append("The SLA for solving the ticket is ")
        .append(metadata.get(FieldName.SLA.toString()))
        .append(". The ticket has been assigned to the ")
        .append(metadata.get(FieldName.OWNER_GROUP_NAME_OR_OWNER_EMAIL.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCommentCreationEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.CREATOR_EMAIL.toString()))
        .append("has commented.<br>")
        .append(metadata.get(FieldName.COMMENT_TEXT.toString()))
        .append("<br> <a href=\"")
        .append(metadata.get(FieldName.S3URL.toString()))
        .append("\">Link to Attachment</a>")
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketSeverityChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the severity from ")
        .append(metadata.get(FieldName.OLD_SEVERITY.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.SEVERITY.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketStatusChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append("has changed the status of the ticket from ")
        .append(metadata.get(FieldName.OLD_STATUS.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.STATUS.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketAssigneeChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.LAST_UPDATED_BY_EMAIL.toString()))
        .append("has changed the Assignee from ")
        .append(metadata.get(FieldName.OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketEscalationChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append("The ticket has been escalated to ")
        .append(metadata.get(FieldName.ESCALATION_LEVEL.toString()))
        .append(" escalation owner ")
        .append(metadata.get(FieldName.ESCALATED_TO_EMAIL.toString()))
        .append(" . The ticket needs to be closed on priority. ")
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCcNewPersonAdditionEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append("The ticket has been escalated to ")
        .append(metadata.get(FieldName.ESCALATION_LEVEL.toString()))
        .append(" escalation owner ")
        .append(metadata.get(FieldName.ESCALATED_TO_EMAIL.toString()))
        .append(" . The ticket needs to be closed on priority. ")
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }
}
