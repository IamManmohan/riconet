package com.rivigo.riconet.core.test.Utils;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
public class NotificationDTOModel {

  public static NotificationDTO getNotificationDTO(EventName eventName) {

    Map<String, String> metadata = new HashMap<>();
    metadata.put(TicketingFieldName.CREATOR_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(TicketingFieldName.ASSIGNEE_EMAIL_LIST.toString(), "rameshc10695@gmail.com");
    metadata.put(TicketingFieldName.CC_USER_EMAIL_LIST.toString(), "rameshc10695@gmail.com");
    metadata.put(TicketingFieldName.OWNER_EMAIL_LIST.toString(), "rameshc10695@gmail.com");

    // EventName.TICKET_CREATION
    metadata.put(TicketingFieldName.TICKET_ID.toString(), "1");
    metadata.put(TicketingFieldName.SEVERITY.toString(), "ONE");
    metadata.put(TicketingFieldName.SLA.toString(), "2 Days ");
    metadata.put(
        TicketingFieldName.OWNER_GROUP_NAME_OR_OWNER_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(TicketingFieldName.LAST_UPDATED_BY_EMAIL.toString(), "rameshc10695@gmail.com");

    // EventName.TICKET_ASSIGNEE_CHANGE
    metadata.put(
        TicketingFieldName.OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString(),
        "ramesh.chandra@rivigo.com");
    metadata.put(
        TicketingFieldName.ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString(),
        "rameshc10695@gmail.com");
    metadata.put(TicketingFieldName.TICKET_TYPE.toString(), "QC - CN Validation");

    // EventName.TICKET_STATUS_CHANGE
    metadata.put(TicketingFieldName.OLD_STATUS.toString(), "NEW");
    metadata.put(TicketingFieldName.STATUS.toString(), "IN_PROGRESS");

    // EventName.TICKET_ESCALATION_CHANGE
    metadata.put(TicketingFieldName.ESCALATION_LEVEL.toString(), "TWO");
    metadata.put(TicketingFieldName.ESCALATED_TO_EMAIL.toString(), "rameshc10695@gmail.com");

    // EventName.TICKET_SEVERITY_CHANGE
    metadata.put(TicketingFieldName.OLD_SEVERITY.toString(), "THREE");
    metadata.put(TicketingFieldName.SEVERITY.toString(), "FIVE");

    // EventName.TICKET_CC_NEW_PERSON_ADDITION
    metadata.put(TicketingFieldName.ADDER_EMAIL.toString(), "ramesh.chandra@rivigo.com");
    metadata.put(TicketingFieldName.NEWLY_CCED_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(
        TicketingFieldName.COMMENT_HISTORY.toString(),
        "<p>comment <br><br> ll\tpp\tsscomment1 <br><br> comment2<br><br> comment3</p>");

    // EventName.TICKET_COMMENT_CREATION
    metadata.put(TicketingFieldName.COMMENT_TEXT.toString(), "<p>Comment text</p>");
    metadata.put(TicketingFieldName.S3URL.toString(), "http://s3.url.com");

    return NotificationDTO.builder().eventName(eventName).metadata(metadata).build();
  }

  public static NotificationDTO getNotificationDTOForTICKET_CC_NEW_PERSON_ADDITION() {

    Map<String, String> metadata = new HashMap<>();
    metadata.put("TICKET_ID", "248212");
    metadata.put("NEWLY_CCED_USER_ID", "15146");
    metadata.put("ID", "20");
    metadata.put("CREATED_BY_ID", "5888");
    metadata.put("CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put("SLA", " 109 days 13 hours 18 minutes  Overdue");
    metadata.put("TICKET_TYPE", "Damage");
    metadata.put("ASSIGNEE_EMAIL_LIST", "ramesh.chandra@rivigo.com");
    metadata.put("ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put("ADDER_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("NEWLY_CCED_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "COMMENT_HISTORY",
        "<br><p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:05:40<br><br>Ticket status has been changed from CLOSED To REOPENED<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:07:48<br><br>Ticket status has been changed from REOPENED To CLOSED.<br> Reason: kkk<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:12:45<br><br>Ticket status has been changed from CLOSED To REOPENED<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:39:30<br><br>Ticket status has been changed from REOPENED To CLOSED.<br> Reason: jjhjnm<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:42:21<br><br>Ticket status has been changed from CLOSED To REOPENED<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:54:27<br><br>Ticket got assigned to Ramesh Chandra<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:57:39<br><br><p>ppppekoejdkjnf</p><br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 22:59:37<br><br>Ticket got assigned to Mayank Pandey by Ramesh Chandra<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 23:00:09<br><br>Ticket status has been changed from REOPENED To CLOSED.<br> Reason: k<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;21/08/2018 23:00:26<br><br>Ticket status has been changed from CLOSED To REOPENED<br><br></p><p>Ramesh Chandra&emsp;&emsp;ramesh.chandra@rivigo.com&emsp;&emsp;22/08/2018 01:34:59<br><br><p>Hi, <br>\r\nI'm attaching invoice.<br>\r\nPFA,</p>\r\n<p>Thanks.</p><br><br></p></p><br>");

    return NotificationDTO.builder()
        .eventName(EventName.TICKET_CC_NEW_PERSON_ADDITION)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534883216148L)
        .eventUID("TICKET_CC_NEW_PERSON_ADDITION_TICKET_248212_1534883216148")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }

  public static NotificationDTO getNotificationDTOTICKET_ESCALATION_CHANGE() {

    Map<String, String> metadata = new HashMap<>();
    metadata.put("ESCALATION_LEVEL", "LEVEL_THREE");
    metadata.put("TICKET_ID", "248212");
    metadata.put("ESCALATED_TO_ID", "18778");
    metadata.put("ID", "4");
    metadata.put("CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put("SLA", " 109 days 13 hours 42 minutes  Overdue");
    metadata.put("TICKET_TYPE", "Damage");
    metadata.put("ASSIGNEE_EMAIL_LIST", "ramesh.chandra@rivigo.com");
    metadata.put("ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put("ESCALATED_TO_EMAIL", "rameshc10695@gmail.com");
    return NotificationDTO.builder()
        .eventName(EventName.TICKET_ESCALATION_CHANGE)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534872717749L)
        .eventUID("TICKET_ESCALATION_CHANGE_TICKET_248212_1534872717749")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }

  public static NotificationDTO getNotificationDTOTICKET_STATUS_CHANGE() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("LAST_UPDATED_BY_ID", "5888");
    metadata.put("OLD_STATUS", "REOPENED");
    metadata.put("STATUS", "CLOSED");
    metadata.put("TICKET_ID", "248212");
    metadata.put("ID", "248212");
    metadata.put("CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put("SLA", " 109 days 13 hours 58 minutes  Overdue");
    metadata.put("TICKET_TYPE", "Damage");
    metadata.put("ASSIGNEE_EMAIL_LIST", "ramesh.chandra@rivigo.com");
    metadata.put("ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put("LAST_UPDATED_BY_EMAIL", "ramesh.chandra@rivigo.com");
    return NotificationDTO.builder()
        .eventName(EventName.TICKET_STATUS_CHANGE)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534885617210L)
        .eventUID("TICKET_STATUS_CHANGE_TICKET_248212_1534885617210")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }

  public static NotificationDTO getNotificationDTOTICKET_ASSIGNEE_CHANGE() {

    Map<String, String> metadata = new HashMap<>();
    metadata.put("LAST_UPDATED_BY_ID", "5888");
    metadata.put("OLD_ASSIGNEE_ID", "5886");
    metadata.put("ASSIGNEE_TYPE", "USER");
    metadata.put("ASSIGNEE_ID", "18778");
    metadata.put("TICKET_ID", "248212");
    metadata.put("OLD_ASSIGNEE_TYPE", "USER");
    metadata.put("ID", "248212");
    metadata.put("CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put("SLA", " 109 days 14 hours 5 minutes  Overdue");
    metadata.put("TICKET_TYPE", "Damage");
    metadata.put("ASSIGNEE_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "rameshc10695@gmail.com");
    metadata.put("OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put("LAST_UPDATED_BY_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "\"mayank.pandey@rivigo.com\"");
    return NotificationDTO.builder()
        .eventName(EventName.TICKET_ASSIGNEE_CHANGE)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534886058721L)
        .eventUID("TICKET_ASSIGNEE_CHANGE_TICKET_248212_1534886058721")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }

  public static NotificationDTO getNotificationDTOTICKET_SEVERITY_CHANGE() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("LAST_UPDATED_BY_ID", "5888");
    metadata.put("OLD_SEVERITY", "ONE");
    metadata.put("SEVERITY", "TWO");
    metadata.put("TICKET_ID", "248212");
    metadata.put("ID", "248212");
    metadata.put("CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        "CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put("SLA", " 109 days 13 hours 58 minutes  Overdue");
    metadata.put("TICKET_TYPE", "Damage");
    metadata.put("ASSIGNEE_EMAIL_LIST", "ramesh.chandra@rivigo.com");
    metadata.put("ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "ramesh.chandra@rivigo.com");
    metadata.put("OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put("OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put("LAST_UPDATED_BY_EMAIL", "ramesh.chandra@rivigo.com");
    return NotificationDTO.builder()
        .eventName(EventName.TICKET_STATUS_CHANGE)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534885617210L)
        .eventUID("TICKET_SEVERITY_CHANGE_TICKET_248212_1534885617210")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }

  public static NotificationDTO getNotificationDTOTICKET_COMMENT_CREATION() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        " COMMENT_TEXT",
        "<p>Hi Everyone,<br>\r\nThis is just a lat night commit.</p>\r\n<p>Thanks</p>");
    metadata.put(" TICKET_ID", "248212");
    metadata.put(" ID", "753256");
    metadata.put(" CREATED_BY_ID", "5888");
    metadata.put(" CREATOR_EMAIL", "rameshc10695@gmail.com");
    metadata.put(
        " CC_USER_EMAIL_LIST",
        "mayank.pandey@rivigo.com,nikhil.rawat@rivigo.com,rameshc10695@gmail.com");
    metadata.put(" SLA", " 109 days 14 hours 15 minutes  Overdue");
    metadata.put(" TICKET_TYPE", "Damage");
    metadata.put(" ASSIGNEE_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put(" ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL", "rameshc10695@gmail.com");
    metadata.put(" OWNER_EMAIL_LIST", "rameshc10695@gmail.com");
    metadata.put(" OWNER_GROUP_NAME_OR_OWNER_EMAIL", "rameshc10695@gmail.com");
    metadata.put(" COMMENTOR_EMAIL", "ramesh.chandra@rivigo.com");

    return NotificationDTO.builder()
        .eventName(EventName.TICKET_COMMENT_CREATION)
        .entityId(248212L)
        .entityName("TICKET")
        .eventGUID("TICKET_248212")
        .tsMs(1534886618376L)
        .eventUID("TICKET_COMMENT_CREATION_TICKET_248212_1534886618376")
        .metadata(metadata)
        .conditions(new ArrayList<>())
        .build();
  }
}
