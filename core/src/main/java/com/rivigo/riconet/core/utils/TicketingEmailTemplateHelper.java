package com.rivigo.riconet.core.utils;

import com.rivigo.riconet.core.enums.FieldName;
import com.rivigo.riconet.core.enums.FieldName.Ticketing;
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

    getValueFromMap(metadata, FieldName.Ticketing.TICKET_ID)
        .ifPresent(v -> subject.append("Ticket ").append(v));
    getValueFromMap(metadata, FieldName.Ticketing.TICKET_TYPE)
        .ifPresent(v -> subject.append(" | ").append(v));
    return subject.toString();
  }

  public static Optional<List<String>> getRecipientList(Map<String, String> metadata) {

    List<String> emailList = new ArrayList<>();

    getEmailList(metadata, FieldName.Ticketing.CREATOR_EMAIL).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.Ticketing.ASSIGNEE_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.Ticketing.CC_USER_EMAIL_LIST).ifPresent(emailList::addAll);
    getEmailList(metadata, FieldName.Ticketing.OWNER_EMAIL_LIST).ifPresent(emailList::addAll);

    if (CollectionUtils.isEmpty(emailList)) {
      return Optional.empty();
    }
    return Optional.of(emailList);
  }

  public static Optional<List<String>> getEmailList(Map<String, String> map, Ticketing fieldName) {

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

  public static Optional<String> getValueFromMap(Map<String, String> map, Ticketing fieldName) {
    if (!map.containsKey(fieldName.toString())) {
      return Optional.empty();
    }
    return Optional.of(map.get(fieldName.toString()));
  }

  public static String getTicketCreationEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.CREATOR_EMAIL.toString()))
        .append(" has created a new ticket of the issue type ")
        .append(metadata.get(FieldName.Ticketing.TICKET_TYPE.toString()))
        .append(". The severity of the ticket is ")
        .append(metadata.get(FieldName.Ticketing.SEVERITY.toString()))
        .append(". The SLA for solving the ticket is ")
        .append(metadata.get(FieldName.Ticketing.SLA.toString()))
        .append(". The ticket has been assigned to the ")
        .append(metadata.get(FieldName.Ticketing.OWNER_GROUP_NAME_OR_OWNER_EMAIL.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCommentCreationEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.CREATOR_EMAIL.toString()))
        .append(" has commented.<br>")
        .append(metadata.get(FieldName.Ticketing.COMMENT_TEXT.toString()))
        .append("<br> <a href=\"")
        .append(metadata.get(FieldName.Ticketing.S3URL.toString()))
        .append("\">Link to Attachment</a>")
        .append("<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketSeverityChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the severity from ")
        .append(metadata.get(FieldName.Ticketing.OLD_SEVERITY.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.Ticketing.SEVERITY.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketStatusChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the status of the ticket from ")
        .append(metadata.get(FieldName.Ticketing.OLD_STATUS.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.Ticketing.STATUS.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketAssigneeChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.LAST_UPDATED_BY_EMAIL.toString()))
        .append(" has changed the Assignee from ")
        .append(metadata.get(FieldName.Ticketing.OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append(" to ")
        .append(metadata.get(FieldName.Ticketing.ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString()))
        .append(".<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketEscalationChangeEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append("The ticket has been escalated to escalation level : ")
        .append(metadata.get(FieldName.Ticketing.ESCALATION_LEVEL.toString()))
        .append(" and escalation owner : ")
        .append(metadata.get(FieldName.Ticketing.ESCALATED_TO_EMAIL.toString()))
        .append(" . The ticket needs to be closed on priority. ")
        .append("<br><br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }

  public static String getTicketCcNewPersonAdditionEmailBody(Map<String, String> metadata) {
    return new StringBuilder()
        .append(metadata.get(FieldName.Ticketing.ADDER_EMAIL.toString()))
        .append(" has added ")
        .append(metadata.get(FieldName.Ticketing.NEWLY_CCED_EMAIL.toString()))
        .append(" in CC to this ticket. ")
        .append("<br><br> Comment history : <br>")
        .append(metadata.get(FieldName.Ticketing.COMMENT_HISTORY.toString()))
        .append("<br>Regards,<br>Rivigo Tickets<br>")
        .toString();
  }
}
