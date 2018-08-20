package com.rivigo.riconet.core.test.Utils;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.TicketingFieldName;
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
        "<p>comment <br><br> ll\tpp\tss\n comment1 <br><br> comment2<br><br> comment3</p>");

    // EventName.TICKET_COMMENT_CREATION
    metadata.put(TicketingFieldName.COMMENT_TEXT.toString(), "<p>Comment text</p>");
    metadata.put(TicketingFieldName.S3URL.toString(), "http://s3.url.com");

    return NotificationDTO.builder().eventName(eventName).metadata(metadata).build();
  }
}
