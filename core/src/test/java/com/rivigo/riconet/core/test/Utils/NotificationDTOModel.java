package com.rivigo.riconet.core.test.Utils;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.FieldName;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ramesh
 * @date 20-Aug-2018
 */
public class NotificationDTOModel {

  public static NotificationDTO getNotificationDTO(EventName eventName) {

    Map<String, String> metadata = new HashMap<>();
    metadata.put(FieldName.CREATOR_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(FieldName.ASSIGNEE_EMAIL_LIST.toString(), "rameshc10695@gmail.com");
    metadata.put(FieldName.CC_USER_EMAIL_LIST.toString(), "rameshc10695@gmail.com");
    metadata.put(FieldName.OWNER_EMAIL_LIST.toString(), "rameshc10695@gmail.com");

    //EventName.TICKET_CREATION
    metadata.put(FieldName.TICKET_ID.toString(), "1");
    metadata.put(FieldName.SEVERITY.toString(), "ONE");
    metadata.put(FieldName.SLA.toString(), "2 Days ");
    metadata.put(FieldName.OWNER_GROUP_NAME_OR_OWNER_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(FieldName.LAST_UPDATED_BY_EMAIL.toString(), "rameshc10695@gmail.com");

    //EventName.TICKET_ASSIGNEE_CHANGE
    metadata.put(FieldName.OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString(), "ramesh.chandra@rivigo.com");
    metadata.put(FieldName.ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL.toString(), "rameshc10695@gmail.com");
    metadata.put(FieldName.TICKET_TYPE.toString(), "QC - CN Validation");

    //EventName.TICKET_STATUS_CHANGE
    metadata.put(FieldName.OLD_STATUS.toString(), "NEW");
    metadata.put(FieldName.STATUS.toString(), "IN_PROGRESS");

    //EventName.TICKET_ESCALATION_CHANGE
    metadata.put(FieldName.ESCALATION_LEVEL.toString(), "TWO");
    metadata.put(FieldName.ESCALATED_TO_EMAIL.toString(),"rameshc10695@gmail.com");

    //EventName.TICKET_SEVERITY_CHANGE
    metadata.put(FieldName.OLD_SEVERITY.toString(),"THREE");
    metadata.put(FieldName.SEVERITY.toString(),"FIVE");

    //EventName.TICKET_CC_NEW_PERSON_ADDITION
    metadata.put(FieldName.ADDER_EMAIL.toString(),"ramesh.chandra@rivigo.com");
    metadata.put(FieldName.NEWLY_CCED_EMAIL.toString(),"rameshc10695@gmail.com");
    metadata.put(FieldName.COMMENT_HISTORY.toString(),"<p>comment <br><br> comment1 <br><br> comment2<br><br> comment3</p>");

    //EventName.TICKET_COMMENT_CREATION
    metadata.put(FieldName.COMMENT_TEXT.toString(), "<p>Comment text</p>");
    metadata.put(FieldName.S3URL.toString(),"http://s3.url.com");

    return NotificationDTO.builder().eventName(eventName).metadata(metadata).build();
  }
}
